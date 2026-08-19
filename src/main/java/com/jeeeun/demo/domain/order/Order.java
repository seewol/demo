package com.jeeeun.demo.domain.order;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.shipping.Shipping;
import com.jeeeun.demo.domain.shipping.ShippingStatus;
import com.jeeeun.demo.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user", "orderItems", "shipping"}) // 연관 관계 무한 루프 방지!
@Entity
@Table(name = "orders") // order → SQL 예약어
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 주문 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    // 주문 총 금액 (모든 OrderItem의 최종 결제 금액 합계)
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Order : Shipping → 1:1 관계 (FK는 Shipping이 들고 있음, mappedBy = 주인 아님을 표시)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipping shipping;

    @Column(name = "imp_uid")
    private String impUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason") // 취소 사유 (취소 안 된 주문은 null)
    private CancelReason cancelReason;

    // ─ 가상계좌(무통장입금) 정보 ─
    // 카드결제 등 다른 결제수단인 주문은 이 4개 컬럼이 전부 null
    // (별도 도메인 안 뽑고 Order에 nullable 컬럼으로 둔 이유: 지금 규모에선 이게 훨 단순)
    @Column(name = "vbank_name")
    private String vbankName;      // 가상계좌 은행명 (예: 신한은행)

    @Column(name = "vbank_num")
    private String vbankNum;       // 가상계좌 번호

    @Column(name = "vbank_holder")
    private String vbankHolder;    // 예금주명

    @Column(name = "vbank_due_date")
    private LocalDateTime vbankDueDate;  // 입금기한 (이 시각 지나면 계좌 만료)


    public static Order from(User user, BigDecimal totalPrice, String impUid) {
        Order order = new Order();
        order.user = user;
        order.status = OrderStatus.PAID; // 포트원 결제 검증 통과 후 생성되므로 PAID
        order.totalPrice = totalPrice;
        order.impUid = impUid;
        return order;
    }


    // ★ 가상계좌(무통장입금) 주문 생성
    // 프론트에서 IMP.request_pay(pay_method: 'vbank')로
    // 계좌가 '발급'만 된 상태(아직 미입금)로 들어오는 케이스.
    // 그래서 PAID가 아니라 PENDING으로 생성함.
    public static Order fromVirtualAccount(
            User user,
            BigDecimal totalPrice,
            String impUid,
            String vbankName,
            String vbankNum,
            String vbankHolder,
            LocalDateTime vbankDueDate
    ) {
        Order order = new Order();
        order.user = user;
        order.status = OrderStatus.PENDING; // 아직 입금 전 (계좌만 발급됨)
        order.totalPrice = totalPrice;
        order.impUid = impUid;
        order.vbankName = vbankName;
        order.vbankNum = vbankNum;
        order.vbankHolder = vbankHolder;
        order.vbankDueDate = vbankDueDate;
        return order;
    }


    // ★ 가상계좌 입금 완료 처리 (Webhook에서 호출)
    // PENDING 상태일 때만 PAID로 전환하는 규칙.
    // 이미 PAID거나 CANCELLED인 주문에 웹훅이 중복으로 와도 예외 처리 없이 조용히 무시한다.
    // (포트원은 우리 서버가 200을 안 주면 같은 웹훅을 재시도하기 때문에, 중복 호출 자체는 정상)
    public void completePayment() {
        if (this.status == OrderStatus.PENDING) {
            this.status = OrderStatus.PAID;
        }
    }


    // ★ 주문 상품 전체 취소
    public void cancel(CancelReason reason) {
        // 취소는 PENDING, PAID 상태에서만 가능
        if (this.getStatus() != OrderStatus.PENDING && this.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_ORDER);
        }

        // 배송이 이미 시작됐으면 취소 불가 (SHIPPING/DELIVERED) ─ 이후엔 반품 처리로 진행
        // shipping null 체크도 한 번 해 주기
        if (this.shipping != null
                && (this.shipping.getStatus() == ShippingStatus.SHIPPING
                || this.shipping.getStatus() == ShippingStatus.DELIVERED)) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_SHIPPED_ORDER);
        }

        // 개별 아이템들도 같이 취소 처리
        // (이미 부분취소된 아이템은 남은 수량만큼만)
        for (OrderItem item : this.orderItems) {
            long remainingQuantity = item.getQuantity() - item.getCancelledQuantity();
            if (remainingQuantity > 0) {
                item.cancel(remainingQuantity); // OrderItem.cancel() 재사용 → cancelledQuantity/status 자동 갱신
            }
            // remainingQuantity == 0인 아이템(이미 전량 취소된)은 건너뜀
            // 안 그러면 OrderItem.cancel() 안의 "이미 CANCELLED면 예외" 규칙에 걸림!
        }

        // 배송도 같이 취소 처리
        // 위에서 이미 SHIPPING/DELIVERED 걸러져서, 여기 도달했으면 PREPARING/DELAYED 상태일 것.
        if (this.shipping != null) {
            this.shipping.cancel();
        }

        this.status = OrderStatus.CANCELLED;
        this.cancelReason = reason;
    }


    // NOTE : 서비스에서 orderItem.cancel() 직접 안부르고, Order.cancelItem() 하는 이유
    // Order가 OrderItem을 소유한 '루트'이고 상품 하나가 취소됐을 때,
    // '나머지 상품들도 다 취소됐는지' 판단해 Order 전체 상태를 바꾸는 규칙은
    // OrderItem 혼자서 알 수 없고, Order만 알 수 있는 정보이기 때문이다.
    // → 고로 해당 판단 로직은 Order가 갖고 있음이 마땅!


    // ★ 주문 상품 부분 취소 (아이템 단위)
    // 1 : 취소 대상 아이템을 찾아서 자신 규칙대로 취소할 것 (OrderItem.cancel(cancelQuantity))
    // 2 : 취소 후 남은 아이템이 전부 취소 상태면 Order 전체도 자동으로 CANCELLED 전환
    public void cancelItem(Long orderItemId, long cancelQuantity) {

        if (this.status != OrderStatus.PENDING && this.status != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_ORDER);
        }

        OrderItem targetItem = this.orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER_ITEM));

        targetItem.cancel(cancelQuantity);

        boolean allCancelled = this.orderItems.stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.CANCELLED);

        if (allCancelled) {
            this.status = OrderStatus.CANCELLED;
        }

    }


    // ★ 연관관계 편의 메서드 ─ Shipping.from() 내부에서만 호출
    // 단순히 양방향 연관관계를 잇는 용도이므로 "규칙"이 없다. (고로 예외 처리도 없음)
    public void assignShipping(Shipping shipping) {
        this.shipping = shipping;
    }


    // NOTE : ▼ 도메인 메서드 방식
    // "취소 가능한지"를 Order 스스로가 알고 있도록 함
    // 어디에서 호출해도 규칙이 항상 동일하게 적용됨

    // 서비스가 규칙을 아는 것이 아닌,
    // "엔티티가 자기 규칙을 스스로 지키는 것" → 객체지향의 핵심

    // NOTE : 도메인 메서드가 필요한 경우
    // 엔티티 상태 변경 시, 비즈니스 규칙이 따라오는 경우
    // 1. stock.decrease(quantity); 재고 차감 → 0 미만이면 안 되는 규칙
    // 2. order.cancel(); 주문 취소 → PENDING&PAID 상태에서만 가능한 규칙
    // 3. user.withdraw(); 회원 탈퇴 → 삭제 시각 기록, 개인정보 null 처리 규칙

    // ★ 단순 조회/저장/삭제의 경우 규칙이 없기 때문에 서비스에서 직접 다뤄도 됨

}

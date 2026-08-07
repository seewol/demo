package com.jeeeun.demo.domain.order;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.shipping.Shipping;
import com.jeeeun.demo.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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


    public static Order from(User user, BigDecimal totalPrice, String impUid) {
        Order order = new Order();
        order.user = user;
        order.status = OrderStatus.PAID; // 포트원 결제 검증 통과 후 생성되므로 PAID
        order.totalPrice = totalPrice;
        order.impUid = impUid;
        return order;
    }

    // ★ 주문 상품 전체 취소
    public void cancel() {
        // 취소는 PENDING, PAID 상태에서만 가능
        if (this.getStatus() != OrderStatus.PENDING && this.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_ORDER);
        }
        this.status = OrderStatus.CANCELLED;
    }


    // NOTE : 서비스에서 orderItem.cancel() 직접 안부르고, Order.cancelItem() 하는 이유
    // Order가 OrderItem을 소유한 '루트'이고 상품 하나가 취소됐을 때,
    // '나머지 상품들도 다 취소됐는지' 판단해 Order 전체 상태를 바꾸는 규칙은
    // OrderItem 혼자서 알 수 없고, Order만 알 수 있는 정보이기 때문이다.
    // → 고로 해당 판단 로직은 Order가 갖고 있음이 마땅!

    // ★ 주문 상품 부분 취소 (아이템 단위)
    // 1 : 취소 대상 아이템을 찾아서 자신 규칙대로 취소할 것 (OrderItem.cancel())
    // 2 : 취소 후 남은 아이템이 전부 취소 상태면 Order 전체도 자동으로 CANCELLED 전환
    public void cancelItem(Long orderItemId) {

        if(this.status != OrderStatus.PENDING && this.status != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_ORDER);
        }

        OrderItem targetItem = this.orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER_ITEM));

        targetItem.cancel();

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

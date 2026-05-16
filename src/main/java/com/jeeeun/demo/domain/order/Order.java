package com.jeeeun.demo.domain.order;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user", "orderItems"}) // 연관 관계 무한 루프 방지!
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


    public void cancel() {
        // 취소는 PENDING, PAID 상태에서만 가능
        if (this.getStatus() != OrderStatus.PENDING && this.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_ORDER);
        }
        this.status = OrderStatus.CANCELLED;
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

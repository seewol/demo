package com.jeeeun.demo.domain.order;

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

    public static Order from(User user, BigDecimal totalPrice) {
        Order order = new Order();
        order.user = user;
        order.status = OrderStatus.PENDING; // 생성 시 기본 값은 '주문 완료'
        order.totalPrice = totalPrice;
        return order;
    }

}

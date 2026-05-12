package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderCreateResult(

        Long orderId,
        OrderStatus status,     // 주문 상태 (생성 직후에는 PENDING)
        BigDecimal totalPrice,  // 총 주문 금액
        LocalDateTime createdAt  // 주문 생성 시각

) {
    public static OrderCreateResult from(Order order) {
        return OrderCreateResult.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

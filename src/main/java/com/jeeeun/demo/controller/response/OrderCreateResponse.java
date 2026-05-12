package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.order.OrderStatus;
import com.jeeeun.demo.service.order.model.OrderCreateResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderCreateResponse(

        Long orderId,
        OrderStatus status,     // 주문 상태 (생성 직후에는 PENDING)
        BigDecimal totalPrice,  // 총 주문 금액
        LocalDateTime createdAt  // 주문 생성 시각

) {
    public static OrderCreateResponse from(OrderCreateResult result) {
        return OrderCreateResponse.builder()
                .orderId(result.orderId())
                .status(result.status())
                .totalPrice(result.totalPrice())
                .createdAt(result.createdAt())
                .build();
    }
}

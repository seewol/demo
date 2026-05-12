package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.controller.response.OrderCancelResponse;
import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCancelResult(

        Long orderId,
        String status,     // CANCELLED
        LocalDateTime updatedAt

) {
    public static OrderCancelResult from(Order order) {
        return OrderCancelResult.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())   // enum → String 변환은 .name()
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}

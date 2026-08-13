package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.order.model.OrderCancelResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCancelResponse(

        Long id,
        String status,
        String reasonCode,
        String reasonDescription,
        LocalDateTime updatedAt

) {
    public static OrderCancelResponse from(OrderCancelResult result) {
        return OrderCancelResponse.builder()
                .id(result.orderId())
                .status(result.status())
                .reasonCode(result.reason().name())
                .reasonDescription(result.reason().getDescription())
                .updatedAt(result.updatedAt())
                .build();
    }

}
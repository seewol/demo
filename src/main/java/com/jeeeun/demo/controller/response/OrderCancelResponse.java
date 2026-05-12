package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.order.model.OrderCancelResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCancelResponse(

        Long orderId,
        String status,  // CANCELLED 고정
        LocalDateTime updatedAt

) {
    public static OrderCancelResponse from(OrderCancelResult result) {
        return OrderCancelResponse.builder()
                .orderId(result.orderId())
                .status(result.status())
                .updatedAt(result.updatedAt())
                .build();
    }

}

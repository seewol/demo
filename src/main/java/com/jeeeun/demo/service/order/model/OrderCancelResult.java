package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.CancelReason;
import com.jeeeun.demo.domain.order.Order;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCancelResult(

        Long orderId,
        String status,          // CANCELLED
        CancelReason reason,    // 취소 사유 (enum 그대로)
        LocalDateTime updatedAt

) {
    public static OrderCancelResult from(Order order) {
        return OrderCancelResult.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .reason(order.getCancelReason())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
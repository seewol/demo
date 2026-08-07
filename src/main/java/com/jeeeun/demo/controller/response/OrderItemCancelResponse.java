package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.order.model.OrderItemCancelResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderItemCancelResponse(

        Long orderItemId,
        String itemStatus,
        String orderStatus,
        long cancelledQuantity,
        long remainingQuantity,
        LocalDateTime updatedAt

) {
    public static OrderItemCancelResponse from(OrderItemCancelResult result) {
        return OrderItemCancelResponse.builder()
                .orderItemId(result.orderItemId())
                .itemStatus(result.itemStatus())
                .orderStatus(result.orderStatus())
                .cancelledQuantity(result.cancelledQuantity())
                .remainingQuantity(result.remainingQuantity())
                .updatedAt(result.updatedAt())
                .build();
    }

}

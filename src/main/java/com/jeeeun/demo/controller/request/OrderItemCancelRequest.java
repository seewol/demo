package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.order.model.OrderItemCancelCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderItemCancelRequest(

        @NotNull
        @Min(1)
        Long cancelQuantity

) {
    public OrderItemCancelCommand toCommand(Long orderId, Long orderItemId, Long userId) {
        return OrderItemCancelCommand.builder()
                .orderId(orderId)
                .orderItemId(orderItemId)
                .userId(userId)
                .cancelQuantity(cancelQuantity)
                .build();
    }

}

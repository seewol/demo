package com.jeeeun.demo.service.order.model;

import lombok.Builder;

@Builder
public record OrderItemCancelCommand(

        Long orderId,
        Long orderItemId,
        Long userId,
        Long cancelQuantity

) {}

package com.jeeeun.demo.service.cart.model;

import lombok.Builder;

@Builder
public record CartItemUpdateCommand(
        Long userId,
        Long cartItemId,
        long quantity
) {

}

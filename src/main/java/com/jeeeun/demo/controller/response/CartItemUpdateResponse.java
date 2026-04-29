package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.cart.model.CartItemUpdateResult;
import lombok.Builder;

@Builder
public record CartItemUpdateResponse(
        Long cartItemId,
        long quantity
) {
    public static CartItemUpdateResponse from(CartItemUpdateResult result) {
        return CartItemUpdateResponse.builder()
                .cartItemId(result.cartItemId())
                .quantity(result.quantity())
                .build();
    }
}

package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.cart.model.CartItemUpdateResult;
import lombok.Builder;

@Builder
public record CartItemUpdateResponse(
        Long id,
        long quantity
) {
    public static CartItemUpdateResponse from(CartItemUpdateResult result) {
        return CartItemUpdateResponse.builder()
                .id(result.cartItemId())
                .quantity(result.quantity())
                .build();
    }
}

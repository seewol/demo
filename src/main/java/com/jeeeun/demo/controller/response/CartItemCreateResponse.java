package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.cart.model.CartItemCreateResult;
import lombok.Builder;

@Builder
public record CartItemCreateResponse(
        Long cartItemId,
        Long variantId,
        long quantity
) {
    public static CartItemCreateResponse from(CartItemCreateResult result) {
        return CartItemCreateResponse.builder()
                .cartItemId(result.cartItemId())
                .variantId(result.variantId())
                .quantity(result.quantity())
                .build();
    }
}

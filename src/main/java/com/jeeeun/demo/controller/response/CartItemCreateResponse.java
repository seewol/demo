package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.cart.model.CartItemCreateResult;
import lombok.Builder;

@Builder
public record CartItemCreateResponse(
        Long id,
        Long variantId,
        long quantity
) {
    public static CartItemCreateResponse from(CartItemCreateResult result) {
        return CartItemCreateResponse.builder()
                .id(result.cartItemId())
                .variantId(result.variantId())
                .quantity(result.quantity())
                .build();
    }
}

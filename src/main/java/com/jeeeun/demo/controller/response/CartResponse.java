package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.cart.model.CartItemResult;
import com.jeeeun.demo.service.cart.model.CartResult;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
    Long cartId,
    List<CartItemResult> items
) {
    public static CartResponse from(CartResult result) {
        return CartResponse.builder()
                .cartId(result.cartId())
                .items(result.items())
                .build();
    }
}

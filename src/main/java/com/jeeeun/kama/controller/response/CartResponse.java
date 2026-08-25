package com.jeeeun.kama.controller.response;

import com.jeeeun.kama.service.cart.model.CartItemResult;
import com.jeeeun.kama.service.cart.model.CartResult;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
    Long id,
    List<CartItemResult> items
) {
    public static CartResponse from(CartResult result) {
        return CartResponse.builder()
                .id(result.cartId())
                .items(result.items())
                .build();
    }
}

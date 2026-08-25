package com.jeeeun.kama.service.cart.model;

import com.jeeeun.kama.domain.user.CartItem;
import lombok.Builder;

@Builder
public record CartItemUpdateResult(
        Long cartItemId,
        long quantity
) {
    public static CartItemUpdateResult from(CartItem cartItem) {
        return CartItemUpdateResult.builder()
                .cartItemId(cartItem.getId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}

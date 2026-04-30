package com.jeeeun.demo.service.cart.model;

import com.jeeeun.demo.domain.user.CartItem;
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

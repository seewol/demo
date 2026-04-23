package com.jeeeun.demo.service.cart.model;

import com.jeeeun.demo.domain.user.Cart;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResult(
    Long cartId,
    List<CartItemResult> items
) {
    public static CartResult from(Cart cart) {
        return CartResult.builder()
                .cartId(cart.getId())
                .items(cart.getCartItems().stream()
                        .map(CartItemResult::from)
                        .toList())  // List<CartItemResult> 타입 변환 필요
                .build();
    }
}

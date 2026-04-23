package com.jeeeun.demo.service.cart.model;

import com.jeeeun.demo.domain.user.CartItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemResult(
        Long cartItemId,
        Long variantId,
        String productName,
        BigDecimal salePrice,
        long quantity
) {
    public static CartItemResult from(CartItem cartItem) {
        return CartItemResult.builder()
                .cartItemId(cartItem.getId())
                .variantId(cartItem.getProductVariant().getId())
                .productName(cartItem.getProductVariant().getProduct().getName())
                .salePrice(cartItem.getProductVariant().getProduct().getSalePrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}

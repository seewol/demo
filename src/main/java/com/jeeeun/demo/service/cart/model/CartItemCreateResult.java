package com.jeeeun.demo.service.cart.model;

import com.jeeeun.demo.domain.user.CartItem;
import lombok.Builder;

@Builder
public record CartItemCreateResult(
        Long cartItemId,
        Long variantId,
        long quantity

) {

    // 서비스에서 CartItem 저장 → Result 변환해서 컨트롤러로 리턴
    public static CartItemCreateResult from(CartItem cartItem) {
        return CartItemCreateResult.builder()
                .cartItemId(cartItem.getId())
                .variantId(cartItem.getProductVariant().getId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}

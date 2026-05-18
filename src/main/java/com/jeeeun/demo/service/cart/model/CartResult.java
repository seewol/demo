package com.jeeeun.demo.service.cart.model;

import com.jeeeun.demo.domain.user.Cart;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResult(
    Long cartId,
    List<CartItemResult> items
) {
    public static CartResult from(Cart cart, List<CartItemResult> items) {
        return CartResult.builder()
                .cartId(cart.getId())
                .items(items)  // 이미 변환된 items 리스트 받았으니 그대로 담기
                .build();
    }

    // 기존엔 Cart만 파라미터로 받아, 내부에서 CartItemResult로 변환했는데,
    // isSoldOut 때문에 서비스 안에서 변환 후 넘기는 방식으로 변경하였다.
}

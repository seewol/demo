package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.cart.model.CartItemUpdateCommand;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record CartItemUpdateRequest(

        // NOTE : quantity는 더하고 빼는 개념 아니고, 바꿔치기 할 수량!
        // 프론트에서 + or - 계산 후 최종 수량을 넘겨주는 거라고 생각하면 된다.

        // 재고 관리 (ADMIN) → += 개념  "재고 50개 추가"
        // 장바구니 수량 변경 (USER) → = 개념  "이 수량으로 바꿔치기"

        @NonNull
        @Min(1)
        long quantity

) {
    public CartItemUpdateCommand toCommand(Long userId, Long cartItemId) {
        return CartItemUpdateCommand.builder()
                .userId(userId)
                .cartItemId(cartItemId)
                .quantity(quantity)
                .build();
    }
}

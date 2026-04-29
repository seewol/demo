package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.cart.model.CartItemUpdateCommand;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record CartItemUpdateRequest(

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

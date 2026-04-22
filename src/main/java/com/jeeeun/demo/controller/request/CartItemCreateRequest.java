package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.cart.model.CartItemCreateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CartItemCreateRequest(

        @NotNull
        Long variantId,

        @NotNull
        @Min(value = 1)
        long quantity
) {

    // userId는 컨트롤러에서 인증 정보 꺼내서 넘겨줄 것
    public CartItemCreateCommand toCommand(Long userId) {
        return CartItemCreateCommand.builder()
                .userId(userId)
                .variantId(variantId)
                .quantity(quantity)
                .build();
    }
}

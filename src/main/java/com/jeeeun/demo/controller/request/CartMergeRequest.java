package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.cart.model.CartMergeCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartMergeRequest( // 장바구니 아이템 목록 전체

        @NotEmpty
        @Valid
        List<Item> items

) {
    // CartMergeItemRequest를 내부 레코드로 정의한 셈
    // 왜냐? CartMergeRequest 안에서만 쓰이는 아이템 구조라서!
    public record Item(

            @NotNull(message = "variantId는 필수입니다")
            Long variantId,

            @NotNull(message = "수량은 1개 이상이어야 합니다.")
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
            Long quantity

    ) {}

    public CartMergeCommand toCommand(Long userId) {

        List<CartMergeCommand.Item> commandItems = items.stream()
                .map(i -> new CartMergeCommand.Item(i.variantId(), i.quantity()))
                .toList();

        return new CartMergeCommand(userId, commandItems);
    }
}

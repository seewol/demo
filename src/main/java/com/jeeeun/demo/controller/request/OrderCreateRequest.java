package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.order.model.OrderCreateCommand;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateRequest(

        // ★ 주문할 장바구니 속 아이템 ID 목록
        // NOTE : 최소 1개 이상 아이템 선택 시 주문 가능
        // null도 안 되고, 빈 리스트도 안 됨!
        @NotEmpty(message = "주문할 상품을 선택해주세요.")
        List<Long> cartItemIds

) {
    public OrderCreateCommand toCommand(Long userId) {
        return OrderCreateCommand.builder()
                .userId(userId)
                .cartItemIds(cartItemIds)
                .build();
    }
}

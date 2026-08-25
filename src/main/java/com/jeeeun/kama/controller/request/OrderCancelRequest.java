package com.jeeeun.kama.controller.request;

import com.jeeeun.kama.domain.order.CancelReason;
import com.jeeeun.kama.service.order.model.OrderCancelCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderCancelRequest(

        @NotNull(message = "취소 사유를 선택해주세요.")
        CancelReason reason

) {

    public OrderCancelCommand toCommand(Long orderId, Long userId) {
        return OrderCancelCommand.builder()
                .orderId(orderId)
                .userId(userId)
                .reason(reason)
                .build();
    }
}
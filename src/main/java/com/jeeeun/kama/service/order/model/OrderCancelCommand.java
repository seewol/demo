package com.jeeeun.kama.service.order.model;

import com.jeeeun.kama.domain.order.CancelReason;
import lombok.Builder;

@Builder
public record OrderCancelCommand(

        Long orderId,
        Long userId,
        CancelReason reason
) {
}
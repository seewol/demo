package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.CancelReason;
import lombok.Builder;

@Builder
public record OrderCancelCommand(

        Long orderId,
        Long userId,
        CancelReason reason
) {
}
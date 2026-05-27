package com.jeeeun.demo.service.order.model;

import lombok.Builder;

@Builder
public record DirectOrderCreateCommand(
        Long userId,
        Long variantId,
        long quantity,
        String impUid
) {
}

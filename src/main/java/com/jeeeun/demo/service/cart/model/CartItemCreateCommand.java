package com.jeeeun.demo.service.cart.model;

import lombok.Builder;

@Builder
public record CartItemCreateCommand(

        Long userId,    // 누가
        Long variantId,  // 어떤 조합 담을 건지
        long quantity   // 수량
) {}

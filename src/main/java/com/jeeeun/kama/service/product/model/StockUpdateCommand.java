package com.jeeeun.kama.service.product.model;

import com.jeeeun.kama.domain.product.Operation;
import lombok.Builder;

@Builder
public record StockUpdateCommand(
        Long variantId,
        Operation operation,
        long quantity
) {}

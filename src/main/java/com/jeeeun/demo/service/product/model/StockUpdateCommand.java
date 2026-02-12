package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.domain.product.Operation;
import lombok.Builder;

@Builder
public record StockUpdateCommand(
        Integer variantId,
        Operation operation,
        long quantity
) {}

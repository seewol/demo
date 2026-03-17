package com.jeeeun.demo.service.product.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductVariantCreateCommand(
        Long productId,
        List<Long> optionDetailIds,
        BigDecimal additionalPrice
) {}

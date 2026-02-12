package com.jeeeun.demo.service.product.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductVariantCreateCommand(
        Integer productId,
        List<Integer> optionDetailIds,
        BigDecimal additionalPrice
) {}

package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.domain.product.ProductVariant;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductVariantCreateResult(
        Long variantId,
        String variantName,
        BigDecimal additionalPrice
) {
    public static ProductVariantCreateResult from(ProductVariant variant) {
        return ProductVariantCreateResult.builder()
                .variantId(variant.getId())
                .variantName(variant.getVariantName())
                .additionalPrice(variant.getAdditionalPrice())
                .build();
    }
}

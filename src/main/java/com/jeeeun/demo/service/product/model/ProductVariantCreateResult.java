package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.controller.request.ProductVariantCreateRequest;
import com.jeeeun.demo.domain.product.ProductVariant;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductVariantCreateResult(
        Integer id,
        String variantName,
        BigDecimal additionalPrice
) {
    public static ProductVariantCreateResult from(ProductVariant variant) {
        return ProductVariantCreateResult.builder()
                .id(variant.getId())
                .variantName(variant.getVariantName())
                .additionalPrice(variant.getAdditionalPrice())
                .build();
    }
}

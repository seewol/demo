package com.jeeeun.kama.controller.response;

import com.jeeeun.kama.service.product.model.ProductCreateResult;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductCreateResponse (
    Long id,
    Long categoryId,
    String name,
    String description,
    BigDecimal salePrice,
    boolean isDiscounted,
    Integer discountRate,
    LocalDateTime createdAt

) {

    public static ProductCreateResponse from(ProductCreateResult result) {
        return ProductCreateResponse.builder()
                .id(result.productId())
                .categoryId(result.categoryId())
                .name(result.name())
                .salePrice(result.salePrice())
                .description(result.description())
                .isDiscounted(result.isDiscounted())
                .discountRate(result.discountRate())
                .createdAt(result.createdAt())
                .build();
    }

}

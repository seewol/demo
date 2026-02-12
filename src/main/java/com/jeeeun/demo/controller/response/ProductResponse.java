package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.product.model.ProductResult;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductResponse (
    Integer id,
    Integer categoryId,
    String name,
    String description,
    BigDecimal salePrice,
    boolean isDiscounted,
    Integer discountRate,
    LocalDateTime discountStartAt,
    LocalDateTime discountEndAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted
) {

    public static ProductResponse from(ProductResult result) {
        return ProductResponse.builder()
                .id(result.id())
                .categoryId(result.categoryId())
                .name(result.name())
                .description(result.description())
                .salePrice(result.salePrice())
                .isDiscounted(result.isDiscounted())
                .discountRate(result.discountRate())
                .discountStartAt(result.discountStartAt())
                .discountEndAt(result.discountEndAt())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .isDeleted(result.isDeleted())
                .build();
    }
}
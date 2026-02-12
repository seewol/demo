package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.domain.product.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductResult(
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

    public static ProductResult from(Product product) {
        return ProductResult.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .name(product.getName())
                .description(product.getDescription())
                .salePrice(product.getSalePrice())
                .isDiscounted(product.isDiscounted())
                .discountStartAt(product.getDiscountStartAt())
                .discountEndAt(product.getDiscountEndAt())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .isDeleted(product.isDeleted())
                .build();
    }
}

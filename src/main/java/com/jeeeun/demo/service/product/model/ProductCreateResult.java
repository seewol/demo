package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.domain.product.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductCreateResult(
        Integer id,
        Integer categoryId,
        String name,
        String description,
        BigDecimal salePrice,
        boolean isDiscounted,
        Integer discountRate,
        LocalDateTime createdAt
) {
    public static ProductCreateResult from(Product product) {
        return ProductCreateResult.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .name(product.getName())
                .description(product.getDescription())
                .salePrice(product.getSalePrice())
                .isDiscounted(product.isDiscounted())
                .discountRate(product.getDiscountRate())
                .createdAt(product.getCreatedAt())
                .build();
    }
}

package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.product.model.ProductCreateResult;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductCreateResponse (
    Integer id,
    Integer categoryId,
    String name,
    String description,
    BigDecimal salePrice,
    boolean isDiscounted,
    Integer discountRate,
    LocalDateTime createdAt

) {

    public static ProductCreateResponse from(ProductCreateResult result) {
        return ProductCreateResponse.builder()
                .id(result.id())
                .categoryId(result.categoryId())
                .name(result.name())
                .salePrice(result.salePrice())
                .isDiscounted(result.isDiscounted())
                .discountRate(result.discountRate())
                .createdAt(result.createdAt())
                .build();
    }

    public static ProductCreateResponse example() {
        return ProductCreateResponse.builder()
                .id(1)
                .categoryId(1)
                .name("후드티")
                .description("박시한 후드티입니다.")
                .salePrice(BigDecimal.valueOf(9000))
                .isDiscounted(true)
                .discountRate(10)
                .createdAt(LocalDateTime.now())
                .build();
    }

}

package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.product.model.ProductCreateResult;
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
                .id(1L) // L 붙여 Long 리터럴 (1은 int 리터럴)
                .categoryId(1L)
                .name("후드티")
                .description("박시한 후드티입니다.")
                .salePrice(BigDecimal.valueOf(9000))
                .isDiscounted(true)
                .discountRate(10)
                .createdAt(LocalDateTime.now())
                .build();
    }

}

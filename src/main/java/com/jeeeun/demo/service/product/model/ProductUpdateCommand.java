package com.jeeeun.demo.service.product.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductUpdateCommand(

        Long productId,
        Long categoryId,
        String name,
        String description,
        BigDecimal salePrice,
        Boolean isDiscounted,
        Integer discountRate,
        LocalDateTime discountStartAt,
        LocalDateTime discountEndAt,
        Integer maxPurchaseQuantity

) {
}

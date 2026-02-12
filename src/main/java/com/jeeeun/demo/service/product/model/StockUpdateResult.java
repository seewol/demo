package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.domain.product.ProductStock;
import lombok.Builder;

@Builder
public record StockUpdateResult(
        Integer variantId,
        long stockQuantity
) {
    public static StockUpdateResult from(ProductStock stock) {
        return StockUpdateResult.builder()
                .variantId(stock.getProductVariant().getId())
                .stockQuantity(stock.getQuantity())
                .build();
    }
}

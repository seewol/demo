package com.jeeeun.kama.service.product.model;

import com.jeeeun.kama.domain.product.ProductStock;
import lombok.Builder;

@Builder
public record StockUpdateResult(
        Long variantId,
        long stockQuantity
) {
    public static StockUpdateResult from(ProductStock stock) {
        return StockUpdateResult.builder()
                .variantId(stock.getProductVariant().getId())
                .stockQuantity(stock.getQuantity())
                .build();
    }
}

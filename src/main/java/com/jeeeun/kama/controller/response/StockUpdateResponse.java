package com.jeeeun.kama.controller.response;

import com.jeeeun.kama.service.product.model.StockUpdateResult;
import lombok.Builder;

@Builder
public record StockUpdateResponse(
    Long variantId,
    long stockQuantity

) {
    public static StockUpdateResponse from(StockUpdateResult result) {
        return new StockUpdateResponse(
                result.variantId(), result.stockQuantity()
        );
    }
}

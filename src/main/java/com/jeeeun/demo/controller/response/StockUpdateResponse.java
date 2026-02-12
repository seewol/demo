package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.product.model.StockUpdateResult;
import lombok.Builder;

@Builder
public record StockUpdateResponse(
    Integer variantId,
    long stockQuantity

) {
    public static StockUpdateResponse from(StockUpdateResult result) {
        return new StockUpdateResponse(
                result.variantId(), result.stockQuantity()
        );
    }
}

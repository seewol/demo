package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.domain.product.Operation;
import com.jeeeun.demo.service.product.model.StockUpdateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record StockUpdateRequest (
        @NotNull
        Operation operation, // SET, INCREASE, DECREASE

        @Min(value = 0, message = "음수는 불가합니다.")
        long quantity
) {
        public StockUpdateCommand toCommand(Integer variantId) {
                return StockUpdateCommand.builder()
                        .variantId(variantId)
                        .operation(operation)
                        .quantity(quantity)
                        .build();
        }

}
package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.product.model.ProductVariantCreateResult;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductVariantCreateResponse(
        Integer id,
        String variantName,
        BigDecimal additionalPrice
) {
    public static ProductVariantCreateResponse from(
            ProductVariantCreateResult result
    ) {
        // 필드가 적을 때는 굳이 Builder 안 쓰기도 함
        // 외려, 생성자 호출이 가장 명확!
        return new ProductVariantCreateResponse(
                result.id(),
                result.variantName(),
                result.additionalPrice()
        );
    }
}

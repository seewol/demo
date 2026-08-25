package com.jeeeun.kama.service.product.model;

import lombok.Builder;

@Builder
public record ProductOptionDetailResult(
    Long optionDetailId,
    String description
) {
}

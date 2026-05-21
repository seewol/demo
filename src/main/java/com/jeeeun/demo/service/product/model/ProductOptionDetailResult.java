package com.jeeeun.demo.service.product.model;

import lombok.Builder;

@Builder
public record ProductOptionDetailResult(
    Long optionDetailId,
    String description
) {
}

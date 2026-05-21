package com.jeeeun.demo.service.product.model;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductOptionResult(
    Long optionId,
    String optionName,
    List<ProductOptionDetailResult> optionDetails
) {
}

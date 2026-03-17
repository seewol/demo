package com.jeeeun.demo.service.product.model;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductOptionResult(
    Long id,
    String optionName,
    List<ProductOptionDetailResult> optionDetails
) {
}

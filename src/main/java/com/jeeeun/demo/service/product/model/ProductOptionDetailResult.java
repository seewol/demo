package com.jeeeun.demo.service.product.model;

import lombok.Builder;

@Builder
public record ProductOptionDetailResult(
    Integer id,
    String description
) {
}

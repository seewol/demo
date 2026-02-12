package com.jeeeun.demo.service.product.model;

import lombok.Builder;

@Builder
public record ProductImageResult(
    Integer id,
    String imageUrl,
    Integer imageOrder
) {}
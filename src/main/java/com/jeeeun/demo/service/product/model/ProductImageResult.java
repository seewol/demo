package com.jeeeun.demo.service.product.model;

import lombok.Builder;

@Builder
public record ProductImageResult(
    Long imageId,
    String imageUrl,
    Integer imageOrder
) {}
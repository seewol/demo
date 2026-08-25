package com.jeeeun.kama.service.product.model;

import lombok.Builder;

@Builder
public record ProductImageResult(
    Long imageId,
    String imageUrl,
    Integer imageOrder
) {}
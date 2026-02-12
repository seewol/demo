package com.jeeeun.demo.service.product.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductCreateCommand(
    Integer categoryId,
    Integer memberId,
    String name,
    String description,
    BigDecimal salePrice,

    boolean isDiscounted,
    Integer discountRate,
    LocalDateTime discountStartAt,
    LocalDateTime discountEndAt,

    // Request → Command 변환 시 매핑(map) 해 주면 됨!
    List<ProductImageCommand> images,
    List<ProductOptionCommand> options

) {
    @Builder
    public record ProductImageCommand(
            String imageUrl,
            Integer imageOrder
    ) {}

    @Builder
    public record ProductOptionCommand(
            String optionName,
            List<String> optionDetails
    ) {}

}

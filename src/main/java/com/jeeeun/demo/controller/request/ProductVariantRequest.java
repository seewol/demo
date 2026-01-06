package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantRequest {

    @NotBlank
    private String variantName;

    private String optionDetail1;
    private String optionDetail2;
    private String optionDetail3;

    @NotNull
    private BigDecimal additionalPrice;

    @NotNull
    private Long stockQuantity;

}

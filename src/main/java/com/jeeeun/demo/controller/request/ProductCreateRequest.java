package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String productName;

    @NotBlank(message = "상품 설명은 필수 입력값입니다.")
    private String productContent;

    @NotNull
    private BigDecimal originalPrice;

    @NotNull
    private BigDecimal salePrice;

    private boolean isDiscounted; // default = false
    private Integer discountRate; // 선택
    private LocalDateTime discountStartAt; // 할인 시작일
    private LocalDateTime discountEndAt; // 할인 종료일

}

package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.product.model.ProductCreateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductCreateRequest (
    // 상품 + 이미지 + 옵션 + 조합 한 번에 저장하는 구조.
    // Post /Products 호출 시 아래 필드를 토대로 한 JSON을 한 번에 보냄

    // 1/11
    // 데이터 전달용 객체(DTO) => 자바 16부터 값 객체(Value Object) 간결하게 표현 문법
    // record -컴파일 타임 자동으로 생성
    // 1. private final
    // 2. constructor 모든 필드를 받는 생성자
    // 3. getter
    // 4. equals, hashCode, toString() 자동생성

    @NotNull
    Long categoryId,

    @NotBlank
    String name,

    @NotBlank
    String description, // 상품 설명

    // 가격은 하나로 통일
    @NotNull    // ┌ 0보다 큰 값만 허용, ┌ 0 불가
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal salePrice,

    boolean isDiscounted,

    // rate, start, end: 할인은 선택적이라 null 허용
    // idDiscounted = true인 경우 반드시 값 필요,
    // 따라서 Service 에서 조건부 검증 추가

    @Min(0)
    @Max(100)
    Integer discountRate,

    LocalDateTime discountStartAt,
    LocalDateTime discountEndAt,

    // 이미지 리스트
    @Valid // <---- 추가 (아래 중첩 레코드 있는 경우에)
    @NotEmpty
    List<ProductImageRequest> images,

    // 옵션 리스트
    @Valid // <---- 추가 (아래 중첩 레코드 있는 경우에)
    @NotEmpty
    List<ProductOptionRequest> options

    ) {


    public ProductCreateCommand toCommand() {
        return ProductCreateCommand.builder()
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .salePrice(salePrice)
                .isDiscounted(isDiscounted)
                .discountRate(discountRate)
                .discountStartAt(discountStartAt)
                .discountEndAt(discountEndAt)
                .images(images.stream()
                        .map(image -> ProductCreateCommand.ProductImageCommand.builder()
                                .imageUrl(image.imageUrl()) // record 라 image.imageUrl()
                                .imageOrder(image.imageOrder())
                                .build()
                        ).toList()
                )
                .options(options.stream()
                        .map(option -> ProductCreateCommand.ProductOptionCommand.builder()
                                .optionName(option.optionName())
                                .optionDetails(option.optionDetails())
                                .build()
                        ).toList()
                ).build();
    }
}

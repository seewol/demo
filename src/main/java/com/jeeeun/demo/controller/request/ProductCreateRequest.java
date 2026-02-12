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
    Integer categoryId,

    @NotNull
    Integer memberId, // 등록자

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
    // (맨 아래 예시 추가해둠)

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

//    // 조합 리스트
//    @Valid // <---- 추가 (아래 중첩 레코드 있는 경우에)
//    @NotEmpty
//    List<ProductVariantRequest> variants

    ) {

    // ======================
    // inner records (내부 레코드) -> 원래의 내부 정적 클래스와 동일한 역할
    // ======================

    @Builder
    public record ProductImageRequest (
            @NotBlank
            String imageUrl,

            @NotNull
            Integer imageOrder
    ) {}

    @Builder
    public record ProductOptionRequest (
        @NotBlank
        String optionName, // 옵션 그룹 이름 (color, size..)

        @NotEmpty
        List<@NotBlank String> optionDetails // 그룹의 세부(구체적인)값들 (red, blue..)
    ) {}

//    @Builder
//    public record ProductVariantRequest (
//
//        @NotBlank
//        String variantName,
//
//        String optionDetail1,
//        String optionDetail2,
//        String optionDetail3, // = 실제 FK ID를 받는 방식
//
//        @NotNull(message = "additionalPrice 입력은 필수입니다.")
//        BigDecimal additionalPrice
//        // DB 에서는 null 가능하지만, 요청 받을 때는 반드시 값이 있어야 안전히 처리 가능
//        // DTO 수준에서는 '0'이라도 꼭 보내라는 의미로 @NotNull 사용
//
//    ) {}

    public ProductCreateCommand toCommand() {
        return ProductCreateCommand.builder()
                .categoryId(categoryId)
                .memberId(memberId)
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
//                )
//                .variants(variants.stream()
//                        .map(variant -> ProductCreateCommand.ProductVariantCommand.builder()
//                                .variantName(variant.variantName())
//                                .optionDetail1(variant.optionDetail1())
//                                .optionDetail2(variant.optionDetail2())
//                                .optionDetail3(variant.optionDetail3())
//                                .additionalPrice(variant.additionalPrice())
//                                .build()
//                        ).toList()
                ).build();
    }
}



/*

[isDiscounted = true]
→ Service 단에서 검증해줘야 함.

[1]
if (request.isDiscounted() && request.getDiscountRate() == null) {
    throw new IllegalArgumentException("할인 적용 시 discountRate는 필수입니다.");
}

[2]
if (request.isDiscounted()) {
    if (request.getDiscountStartAt() == null || request.getDiscountEndAt() == null) {
        throw new IllegalArgumentException("할인 기간은 필수입니다.");
    }
}

 */
package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreateRequest {
    // 상품 + 이미지 + 옵션 + 조합 한 번에 저장하는 구조.
    // Post /Products 호출 시 아래 필드를 토대로 한 JSON을 한 번에 보냄

    @NotNull
    private Integer categoryId;

    @NotNull
    private Integer memberId; // 등록자

    @NotBlank
    private String productName;

    @NotBlank
    private String productContent;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal originalPrice;

    @NotNull    // ┌ 0보다 큰 값만 허용, ┌ 0 불가
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salePrice;

    private boolean isDiscounted = false;

    // rate, start, end: 할인은 선택적이라 null 허용
    // idDiscounted = true인 경우 반드시 값 필요,
    // 따라서 Service 에서 조건부 검증 추가
    // (맨 아래 예시 추가해둠)

    @Min(0)
    @Max(100)
    private Integer discountRate;

    private LocalDateTime discountStartAt;
    private LocalDateTime discountEndAt;

    // 이미지 리스트
    @NotEmpty
    private List<ProductImageRequest> images;

    // 옵션 리스트
    @NotEmpty
    private List<ProductOptionRequest> options;

    // 조합 리스트
    @NotEmpty
    private List<ProductVariantRequest> variants;


    // ▼ 내부 정적 클래스들 (inner static class)

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductImageRequest {
        @NotBlank
        private String imageUrl;

        @NotNull
        private Integer imageOrder;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductOptionRequest {
        @NotBlank
        private String optionName; // 옵션 그룹 이름 (color, size..)

        @NotEmpty
        private List<String> optionDetails; // 그룹의 세부(구체적인)값들 (red, blue..)
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductVariantRequest {

        @NotBlank
        private String variantName;

        private String optionDetail1;
        private String optionDetail2;
        private String optionDetail3; // = 실제 FK ID를 받는 방식

        @NotNull(message = "additionalPrice 입력은 필수입니다.")
        private BigDecimal additionalPrice;
        // DB 에서는 null 가능하지만, 요청 받을 때는 반드시 값이 있어야 안전히 처리 가능
        // DTO 수준에서는 '0'이라도 꼭 보내라는 의미로 @NotNull 사용

        @NotNull
        private Long stockQuantity;
        // long 은 원시형이라 null 자체가 안 들어와 검증 의미가 없음.
        // Entity 에서는 실제 저장용으로 long 사용

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
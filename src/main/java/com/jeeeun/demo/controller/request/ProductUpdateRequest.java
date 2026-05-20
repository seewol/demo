package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.product.model.ProductUpdateCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductUpdateRequest(
    // PATCH: 부분 수정
    // 수정하고 싶은 필드만 보내면 되므로 nullable
    // null로 온 필드 → 변경 안 함 (기존 값 유지)

    // 카테고리 변경
    Long categoryId,

    // 상품명
    @Size(min = 1, max = 255, message = "상품명은 1~255자입니다.")
    String name,

    // 상품 설명
    @Size(min = 1, max = 5000, message = "상품명은 1~5000자입니다.")
    String description,

    // 판매가
    @DecimalMin(value = "0.0", inclusive = false, message = "판매가는 0보다 커야 합니다.")
    BigDecimal salePrice,

    // 할인 여부 (wrapper 사용 → boolean은 null 불가능)
    Boolean isDiscounted,

    // 할인율
    @Min(value = 0, message = "할인율은 0% 이상이어야 합니다.")
    @Max(value = 100, message = "할인율은 100% 이하여야 합니다.")
    Integer discountRate,

    // 할인 시작일
    LocalDateTime discountStartAt,

    // 할인 종료일
    LocalDateTime discountEndAt,

    // 1인당 최대 구매 수량
    @Min(value = 1, message = "최대 구매 수량은 1개 이상입니다.")
    Integer maxPurchaseQuantity

) {

    public ProductUpdateCommand toCommand(Long productId) {
        return ProductUpdateCommand.builder()
                .productId(productId)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .salePrice(salePrice)
                .isDiscounted(isDiscounted)
                .discountRate(discountRate)
                .discountStartAt(discountStartAt)
                .discountEndAt(discountEndAt)
                .maxPurchaseQuantity(maxPurchaseQuantity)
                .build();
    }

}

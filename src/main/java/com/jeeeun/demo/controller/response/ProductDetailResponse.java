package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.product.model.ProductDetailResult;
import com.jeeeun.demo.service.product.model.ProductOptionDetailResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductDetailResponse (

    Integer id,
    Integer categoryId,
    String name,
    String description,
    BigDecimal salePrice,
    boolean isDiscounted,
    Integer discountRate,

    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    List<ProductImageResponse> images,
    List<ProductOptionResponse> options

) {
    @Builder
    public record ProductImageResponse ( // 상품 상세 전용 이미지
        Integer id,
        String imageUrl,
        Integer imageOrder
    ) {}

    @Builder
    public record ProductOptionResponse(
        Integer id,
        String optionName,
        List<String> optionDetails
        // ★ 프론트는 id 필요 없으니 화면에 찍을 문자열만 필요!
    ) {}

    public static ProductDetailResponse from(ProductDetailResult result) {
        return ProductDetailResponse.builder()
                .id(result.id())
                .categoryId(result.categoryId())
                .name(result.name())
                .description(result.description())
                .salePrice(result.salePrice())
                .isDiscounted(result.isDiscounted())
                .discountRate(result.discountRate())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .images(
                        result.images().stream()
                                .map(img -> ProductImageResponse.builder()
                                        .id(img.id())
                                        .imageUrl(img.imageUrl())
                                        .imageOrder(img.imageOrder())
                                        .build())
                                .toList()
                )
                .options(
                        result.options().stream()
                                .map(opt -> ProductOptionResponse.builder()
                                        .id(opt.id())
                                        .optionName(opt.optionName())
                                        .optionDetails(
                                                opt.optionDetails().stream()
                                                        .map(ProductOptionDetailResult::description)
                                                        .toList()
                                        )   // 각 ProductOptionDetailResult 객체에서
                                            // description 필드만 꺼내 List<String> 으로 만듦
                                        .build())
                                .toList()
                )
                .build();
    }
}



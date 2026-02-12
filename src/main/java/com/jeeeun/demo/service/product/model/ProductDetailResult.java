package com.jeeeun.demo.service.product.model;

import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.domain.product.ProductImage;
import com.jeeeun.demo.domain.product.ProductOption;
import com.jeeeun.demo.domain.product.ProductOptionDetail;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Builder
public record ProductDetailResult(
    Integer id,
    Integer categoryId,
    String name,
    String description,
    BigDecimal salePrice,
    boolean isDiscounted,
    Integer discountRate,

    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    List<ProductImageResult> images,
    List<ProductOptionResult> options

) {

    public static ProductDetailResult from(
            Product product,
            List<ProductImage> images,
            Map<Integer, List<ProductOptionDetail>> detailMap) {

        return ProductDetailResult.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .name(product.getName())
                .description(product.getDescription())
                .salePrice(product.getSalePrice())
                .isDiscounted(product.isDiscounted())
                .discountRate(product.getDiscountRate())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .images(
                        images.stream()
                                .sorted(Comparator.comparing(ProductImage::getImageOrder))
                                .map(img -> ProductImageResult.builder()
                                        .id(img.getId())
                                        .imageUrl(img.getImageUrl())
                                        .imageOrder(img.getImageOrder())
                                        .build())
                                .toList()
                )
                .options(
                        product.getProductOptions().stream()
                                .sorted(Comparator.comparing(ProductOption::getId))
                                .map(opt -> ProductOptionResult.builder()
                                        .id(opt.getId())
                                        .optionName(opt.getName())
                                        .optionDetails(          // 옵션 하나의 id와 그에 해당하는 detail 리스트 꺼내. 없으면 List.of()로 빈 리스트 줘.
                                                detailMap.getOrDefault(opt.getId(), List.of()).stream()
                                                        .map(detail -> ProductOptionDetailResult.builder()
                                                                .id(detail.getId())
                                                                .description(detail.getDescription())
                                                                .build())
                                                        .toList()
                                        )
                                        .build())
                                .toList()
                )
                .build();
    }
}

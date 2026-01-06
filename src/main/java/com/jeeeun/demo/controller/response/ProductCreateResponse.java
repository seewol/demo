package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.Category;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor // 상품 등록용
public class ProductCreateResponse {
    Integer productId;
    Category categoryId;
    String productName;
    String productContent;
    BigDecimal originalPrice;
    BigDecimal salePrice;
    boolean isDiscounted;
    Integer discountRate;
    LocalDateTime discountStartAt;
    LocalDateTime discountEndAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean isDeleted;

    public static ProductCreateResponse example() {
        return ProductCreateResponse.builder()
                .productId(1)
                .categoryId(Category.builder()
                        .categoryId(1)
                        .categoryName("상의")
                        .build())
                .productName("후드티")
                .productContent("박시한 후드티입니다.")
                .originalPrice(BigDecimal.valueOf(10000))
                .salePrice(BigDecimal.valueOf(9000))
                .isDiscounted(true)
                .discountRate(10)
                .discountStartAt(LocalDateTime.now())
                .discountEndAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

}

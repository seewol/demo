package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.Category;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor // 상품 목록 조회용
public class ProductResponse {
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
}
package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.Category;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}

package com.jeeeun.demo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {

    private Integer productId;
    private String productName;
    private String productContent;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private boolean isDiscounted;
    private Integer discountRate;

    private LocalDateTime productCreatedAt;
    private LocalDateTime productUpdatedAt;

    private List<ImageResponse> images;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageResponse { // 상품 상세 전용 이미지
        private Integer imageId;
        private String imageUrl;
        private Integer imageOrder;
    }
}

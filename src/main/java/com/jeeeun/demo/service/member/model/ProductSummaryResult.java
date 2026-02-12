package com.jeeeun.demo.service.member.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductSummaryResult(
        Integer productId,
        String productName,
        BigDecimal salePrice,       // 실제 판매가 (노출가)
        boolean isDiscounted,       // 할인 여부(boolean은 null X)
        Integer discountRate       // 할인율
) {}

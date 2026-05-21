package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.order.OrderStatus;
import com.jeeeun.demo.service.order.model.OrderResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(

        Long id,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        LocalDateTime createdAt,

        List<OrderItemResponse> items   // 주문한 상품 목록

) {

    // 주문 목록에서 보여주는 상품 요약
    @Builder
    public record OrderItemResponse(
            String productName,     // 상품명
            String productVariantName,     // 조합명 (ex. "white / M")
            long quantity,       // 수량
            BigDecimal unitPrice,   // 단가
            String thumbnailUrl     // 대표 이미지 URL
    ) {}

    public static OrderResponse from(OrderResult result) {
        return OrderResponse.builder()
                .id(result.orderId())
                .orderStatus(result.orderStatus())
                .totalPrice(result.totalPrice())
                .createdAt(result.createdAt())
                .items(
                        result.items().stream()
                                .map(item -> OrderItemResponse.builder()
                                        .productName(item.productName())
                                        .productVariantName(item.productVariantName())
                                        .quantity(item.quantity())
                                        .unitPrice(item.unitPrice())
                                        .thumbnailUrl(item.thumbnailUrl())
                                        .build())
                                .toList()
                )
                .build();
    }

}

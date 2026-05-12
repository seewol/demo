package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResult(

        Long orderId,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        LocalDateTime createdAt,

        List<OrderDetailItemResult> items

) {

    // 상품 상세
    @Builder
    public record OrderDetailItemResult(
            String productName,
            String variantName,
            long quantity,
            BigDecimal unitPrice,
            String thumbnailUrl
    ) {}

    public static OrderDetailResult from(Order order) {
        return OrderDetailResult.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(order.getOrderItems().stream()
                        .map(item -> OrderDetailItemResult.builder()
                                .productName(item.getProductName())
                                .variantName(item.getProductVariantName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .thumbnailUrl(item.getThumbnailUrl())
                                .build())
                        .toList()
                )
                .build();
    }
}

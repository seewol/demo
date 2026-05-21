package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.order.OrderStatus;
import com.jeeeun.demo.service.order.model.OrderDetailResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResponse(

        Long id,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        LocalDateTime createdAt,

        List<OrderDetailItemResponse> items

) {

    @Builder
    public record OrderDetailItemResponse(
            String productName,
            String variantName,
            long quantity,
            BigDecimal unitPrice,
            String thumbnailUrl
    ) {}

    public static OrderDetailResponse from(OrderDetailResult result) {
        return OrderDetailResponse.builder()
                .id(result.orderId())
                .orderStatus(result.orderStatus())
                .totalPrice(result.totalPrice())
                .createdAt(result.createdAt())
                .items(result.items().stream()
                        .map(item -> OrderDetailItemResponse.builder()
                                .productName(item.productName())
                                .variantName(item.variantName())
                                .quantity(item.quantity())
                                .unitPrice(item.unitPrice())
                                .thumbnailUrl(item.thumbnailUrl())
                                .build())
                        .toList()
                )
                .build();
    }
}

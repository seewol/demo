package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResult(

        Long orderId,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        LocalDateTime createdAt,

        List<OrderItemResult> items

) {
    // OrderItem 한 줄 요약 (주문 목록에서 보여질 정보)
    @Builder
    public record OrderItemResult(
            String productName,         // 상품명
            String productVariantName,  // 조합명 (ex. "white / M")
            long quantity,           // 수량
            BigDecimal unitPrice,       // 단가
            String thumbnailUrl         // 대표 이미지 URL (OrderItem 엔티티에 스냅샷 저장)
    ) {}

    public static OrderResult from(Order order) {
        return OrderResult.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(
                        order.getOrderItems().stream()
                                .map(item -> OrderItemResult.builder()
                                        .productName(item.getProductName())
                                        .productVariantName(item.getProductVariantName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .thumbnailUrl(item.getThumbnailUrl())
                                        .build()
                                )
                                .toList()
                )
                .build();

    }


}

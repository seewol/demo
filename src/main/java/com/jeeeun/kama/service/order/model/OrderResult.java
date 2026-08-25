package com.jeeeun.kama.service.order.model;

import com.jeeeun.kama.domain.order.Order;
import com.jeeeun.kama.domain.order.OrderItemStatus;
import com.jeeeun.kama.domain.order.OrderStatus;
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
            Long orderItemId,           // 부분 취소 API 호출 시 필요
            String productName,         // 상품명 (OrderItem 엔티티에 스냅샷 저장)
            String productVariantName,  // 조합명 (ex. "white / M"), (OrderItem 엔티티에 스냅샷 저장)
            long quantity,              // 수량
            BigDecimal unitPrice,       // 단가 (OrderItem 엔티티에 스냅샷 저장)
            BigDecimal additionalPrice, // 옵션 추가금 (OrderItem 엔티티에 스냅샷 저장)
            String thumbnailUrl,        // 대표 이미지 URL (OrderItem 엔티티에 스냅샷 저장)
            OrderItemStatus status,
            long cancelledQuantity      // 취소된 수량 (누적)
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
                                        .orderItemId(item.getId())
                                        .productName(item.getProductName())
                                        .productVariantName(item.getProductVariantName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .additionalPrice(item.getAdditionalPrice())
                                        .thumbnailUrl(item.getThumbnailUrl())
                                        .status(item.getStatus())
                                        .cancelledQuantity(item.getCancelledQuantity())
                                        .build()
                                )
                                .toList()
                )
                .build();

    }


}

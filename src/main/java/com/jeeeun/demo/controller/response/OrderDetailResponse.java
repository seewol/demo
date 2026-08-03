package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.order.OrderStatus;
import com.jeeeun.demo.domain.shipping.ShippingStatus;
import com.jeeeun.demo.service.order.model.OrderDetailResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResponse(

        Long id,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        LocalDateTime createdAt,

        List<OrderDetailItemResponse> items,
        ShippingResponse shipping

) {

    @Builder
    public record OrderDetailItemResponse(
            String productName,
            String variantName,
            long quantity,
            BigDecimal unitPrice,
            String thumbnailUrl
    ) {}

    @Builder
    public record ShippingResponse(
            Long shippingId,
            String receiverName,
            String receiverPhone,
            String zipCode,
            String address,
            String addressDetail,
            String deliveryRequest,
            BigDecimal shippingFee,
            ShippingStatus status,
            LocalDate expectedShipDate,
            LocalDateTime shippedAt,
            LocalDateTime deliveredAt
    ) {                                 // 중첩 record라 바깥클래스.안쪽클래스 형태
        public static ShippingResponse from(OrderDetailResult.ShippingResult result) {
            return ShippingResponse.builder()
                    .shippingId(result.shippingId())
                    .receiverName(result.receiverName())
                    .receiverPhone(result.receiverPhone())
                    .zipCode(result.zipCode())
                    .address(result.address())
                    .addressDetail(result.addressDetail())
                    .deliveryRequest(result.deliveryRequest())
                    .shippingFee(result.shippingFee())
                    .status(result.status())
                    .expectedShipDate(result.expectedShipDate())
                    .shippedAt(result.shippedAt())
                    .deliveredAt(result.deliveredAt())
                    .build();
        }
    }

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
                // ★ result.shipping() == null일 수 있어서 여기도 null 방어
                .shipping(result.shipping() != null
                        ? ShippingResponse.from(result.shipping())
                        : null)
                .build();
    }
}

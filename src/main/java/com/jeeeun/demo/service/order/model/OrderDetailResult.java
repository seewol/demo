package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderStatus;
import com.jeeeun.demo.domain.shipping.Shipping;
import com.jeeeun.demo.domain.shipping.ShippingStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResult(

        Long orderId,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        LocalDateTime createdAt,

        List<OrderDetailItemResult> items,
        ShippingResult shipping

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

    // 배송 상세
    // delayReason은 내부 관리용 ─ 고객에게는 '지연됨' 대신 expectedShipDate 값만 갱신
    @Builder
    public record ShippingResult(
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
            LocalDateTime shippedAt,    // 배송 시작 전은 null
            LocalDateTime deliveredAt   // 배송 완료 전은 null
    ) {
        public static ShippingResult from(Shipping shipping) {
            return ShippingResult.builder()
                    .shippingId(shipping.getId())
                    .receiverName(shipping.getReceiverName())
                    .receiverPhone(shipping.getReceiverPhone())
                    .zipCode(shipping.getZipCode())
                    .address(shipping.getAddress())
                    .addressDetail(shipping.getAddressDetail())
                    .deliveryRequest(shipping.getDeliveryRequest())
                    .shippingFee(shipping.getShippingFee())
                    .status(shipping.getStatus())
                    .expectedShipDate(shipping.getExpectedShipDate())
                    .shippedAt(shipping.getShippedAt())
                    .deliveredAt(shipping.getDeliveredAt())
                    .build();
        }
    }

    // 주문 상세
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
                // ★ null 방어 : 배송비 리팩토링 이전에 만든 주문은 Shipping이 없을 수도 있음!
                .shipping(order.getShipping() != null
                        ? ShippingResult.from(order.getShipping())
                        : null)
                .build();
    }
}

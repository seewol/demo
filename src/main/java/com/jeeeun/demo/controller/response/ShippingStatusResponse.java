package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.shipping.ShippingStatus;
import com.jeeeun.demo.service.shipping.model.ShippingStatusResult;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ShippingStatusResponse(
        Long shippingId,
        ShippingStatus status,
        LocalDate expectedShipDate,
        String delayReason,     // 고객용엔 노출 X → (ex. OrderDetailResult)
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime updatedAt
) {
    public static ShippingStatusResponse from(ShippingStatusResult result) {
        return ShippingStatusResponse.builder()
                .shippingId(result.shippingId())
                .status(result.status())
                .expectedShipDate(result.expectedShipDate())
                .delayReason(result.delayReason())
                .shippedAt(result.shippedAt())
                .deliveredAt(result.deliveredAt())
                .updatedAt(result.updatedAt())
                .build();
    }
}

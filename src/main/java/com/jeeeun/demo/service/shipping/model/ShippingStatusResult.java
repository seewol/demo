package com.jeeeun.demo.service.shipping.model;

import com.jeeeun.demo.domain.shipping.Shipping;
import com.jeeeun.demo.domain.shipping.ShippingStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ShippingStatusResult(
        Long shippingId,
        ShippingStatus status,
        LocalDate expectedShipDate,
        String delayReason,     // 관리자용이라 여기선 노출! (고객용 OrderDetailResult에는 없음)
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime updatedAt
        // JPA Auditing(@LastModifiedDate)가
        // 엔티티 아무 필드 간에 하나라도 바뀌면 자동으로 updatedAt을 갱신함
) {
    public static ShippingStatusResult from(Shipping shipping) {
        return ShippingStatusResult.builder()
                .shippingId(shipping.getId())
                .status(shipping.getStatus())
                .expectedShipDate(shipping.getExpectedShipDate())
                .delayReason(shipping.getDelayReason())
                .shippedAt(shipping.getShippedAt())
                .deliveredAt(shipping.getDeliveredAt())
                .updatedAt(shipping.getUpdatedAt()) // BaseTimeEntity 상속 중
                .build();
    }
}

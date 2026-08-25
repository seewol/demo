package com.jeeeun.kama.service.shipping.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ShippingDelayCommand(
        // Shipping의 delay()는?
        // 어떤 배송인지(shippingId) + 변경 내용(reason/newExpectedShipDate) → 둘 다 필요.
        // shippingId는 컨트롤러 내 @PathVariable, 나머지는 요청 body에서 옴 → command에서 합칠 것.

        Long shippingId,
        String reason,
        LocalDate newExpectedShipDate

) {
}

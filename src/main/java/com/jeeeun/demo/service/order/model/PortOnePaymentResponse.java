package com.jeeeun.demo.service.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PortOnePaymentResponse(

        @JsonProperty("response")
        PortOnePaymentBody response

) {

    public record PortOnePaymentBody(

            @JsonProperty("imp_uid")
            String impUid,          // 포트원 결제 고유번호 (건마다 발급)

            @JsonProperty("merchant_uid")

            String merchantUid,     // 우리 주문번호 (추후 검증용으로 쓰임)

            BigDecimal amount,      // 실 결제된 금액

            String status           // 결제 상태 (paid, cancelled 등)

    ) {}
}

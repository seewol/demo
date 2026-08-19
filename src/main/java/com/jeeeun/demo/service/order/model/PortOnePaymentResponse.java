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

            BigDecimal amount,      // 결제(예정) 금액 ─ 가상계좌는 '입금해야 할 금액'

            // 결제 상태
            // paid         : 실제로 결제(입금)까지 완료
            // ready        : 가상계좌 발급까지 완료, 아직 입금 전 (무통장입금 전용 상태)
            // cancelled    : 취소/환불됨
            String status,

            // ─ 가상계좌(무통장입금) 전용 필드 ─ 카드결제 등 다른 수단이면 전부 null로 온다.
            @JsonProperty("vbank_num")
            String vbankNum,            // 가상계좌 번호

            @JsonProperty("vbank_name")
            String vbankName,           // 가상계좌 은행명

            @JsonProperty("vbank_holder")
            String vbankHolder,         // 예금주명

            // 입금기한. (* 포트원 V1은 이 값을 Unix Timestamp인 초 단위 숫자로 내려줌 : Long 타입)
            // 예) 1767225600 → LocalDateTime 등 날짜 타입이 아니라서 서비스 레이어에서 변환 필요!
            @JsonProperty("vbank_date")
            Long vbankDate

    ) {}
}

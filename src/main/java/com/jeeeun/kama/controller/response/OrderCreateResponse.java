package com.jeeeun.kama.controller.response;

import com.jeeeun.kama.domain.order.OrderStatus;
import com.jeeeun.kama.service.order.model.OrderCreateResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderCreateResponse(

        Long id,
        OrderStatus status,         // 주문 상태 (결제 완료 후 생성되므로 PAID)
        BigDecimal totalPrice,      // 총 주문 금액
        LocalDateTime createdAt,    // 주문 생성 시각

        // 가상계좌 정보들 (무통장 입금 주문일 때만 값 채워지고, 그 외는 전부 null)
        String vbankName,
        String vbankNum,
        String vbankHolder,
        LocalDateTime vbankDueDate

) {
    public static OrderCreateResponse from(OrderCreateResult result) {
        return OrderCreateResponse.builder()
                .id(result.orderId())
                .status(result.status())
                .totalPrice(result.totalPrice())
                .createdAt(result.createdAt())
                .vbankName(result.vbankName())
                .vbankNum(result.vbankNum())
                .vbankHolder(result.vbankHolder())
                .vbankDueDate(result.vbankDueDate())
                .build();
    }
}

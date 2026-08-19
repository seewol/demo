package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderCreateResult(

        Long orderId,
        OrderStatus status,         // 주문 상태 (카드결제 등 = PAID, 무통장입금(가상계좌) = PENDING)
        BigDecimal totalPrice,      // 총 주문 금액
        LocalDateTime createdAt,    // 주문 생성 시각

        // 가상계좌 정보들 (무통장 입금 주문일 때만 값 채워지고, 그 외는 전부 null)
        // 프론트에서 계좌 입금 안내를 위해 사용
        String vbankName,
        String vbankNum,
        String vbankHolder,
        LocalDateTime vbankDueDate

) {
    public static OrderCreateResult from(Order order) {
        return OrderCreateResult.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .vbankName(order.getVbankName())
                .vbankNum(order.getVbankNum())
                .vbankHolder(order.getVbankHolder())
                .vbankDueDate(order.getVbankDueDate())
                .build();
    }
}

package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderItem;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderItemCancelResult(

        Long orderItemId,
        String itemStatus,      // ORDERED, PARTIALLY_CANCELLED, CANCELLED
        String orderStatus,     // 남은 아이템 전부 취소되면 CANCELLED으로 자동 전환
        long cancelledQuantity, // 해당 아이템 누적 취소 수량
        long remainingQuantity, // 해당 아이템 취소 안 되고 남은 수량
        LocalDateTime updatedAt

) {
    public static OrderItemCancelResult from(Order order, OrderItem item) {
        return OrderItemCancelResult.builder()
                .orderItemId(item.getId())
                .itemStatus(item.getStatus().name())    // enum → String 변환 시 .name()
                .orderStatus(order.getStatus().name())
                .cancelledQuantity(item.getCancelledQuantity())
                .remainingQuantity(item.getQuantity() - item.getCancelledQuantity())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

}


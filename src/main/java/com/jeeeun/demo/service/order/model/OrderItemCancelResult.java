package com.jeeeun.demo.service.order.model;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.domain.order.OrderItem;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderItemCancelResult(

        Long orderItemId,
        String itemStatus,      // CANCELLED으로 고정
        String orderStatus,     // 남은 아이템 전부 취소되면 CANCELLED으로 자동 전환
        LocalDateTime updatedAt

) {
    public static OrderItemCancelResult from(Order order, OrderItem item) {
        return OrderItemCancelResult.builder()
                .orderItemId(item.getId())
                .itemStatus(item.getStatus().name())    // enum → String 변환 시 .name()
                .orderStatus(order.getStatus().name())
                .updatedAt(item.getUpdatedAt()).build();
    }

}


package com.jeeeun.demo.service.order.model;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateCommand(

        Long userId,
        List<Long> cartItemIds,
        String impUid   // 포트원 결제 고유번호

) {
}

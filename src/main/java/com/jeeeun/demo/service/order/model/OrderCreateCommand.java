package com.jeeeun.demo.service.order.model;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateCommand(

        Long userId,
        List<Long> cartItemIds

) {
}

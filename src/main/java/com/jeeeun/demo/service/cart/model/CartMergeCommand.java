package com.jeeeun.demo.service.cart.model;

import java.util.List;

public record CartMergeCommand(

        Long userId,
        List<Item> items
) {

    // Request 내부 레코드와 동일 구조이나,
    // Command는 서비스 레이어용 → Request(컨트롤러)와 Command(서비스)는 역할이 다르므로 분리
    public record Item(
            Long variantId,
            long quantity
    ) {}
}

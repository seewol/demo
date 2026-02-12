package com.jeeeun.demo.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operation {

    SET("Set stock quantity"), // 영어로 씀
    INCREASE("Add to stock"),
    DECREASE("Deduct from stock");

    private final String message;
}

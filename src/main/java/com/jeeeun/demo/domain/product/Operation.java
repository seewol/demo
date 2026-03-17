package com.jeeeun.demo.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operation {

    SET("Set stock quantity"),
    INCREASE("Add to stock"),
    DECREASE("Deduct from stock");

    private final String message;
}

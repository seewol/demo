package com.jeeeun.demo.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 유저
    CONFLICT_USER("이미 등록된 사용자입니다");

    // 상품
    // 존재하지 않은 상품
    //

    // 주문

    private final String message;
}



package com.jeeeun.demo.service.cart.model;

public record CartMergeResult(

        int mergedCount // 머지 성공한 아이템 수

) {
    public static CartMergeResult from(int mergedCount) {
        return new CartMergeResult(mergedCount);
    }
}

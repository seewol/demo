package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.cart.model.CartMergeResult;

public record CartMergeResponse(

        int mergedCount

) {
    public static CartMergeResponse from(CartMergeResult result) {
        return new CartMergeResponse(result.mergedCount());
    }
}

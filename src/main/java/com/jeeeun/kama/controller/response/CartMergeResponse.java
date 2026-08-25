package com.jeeeun.kama.controller.response;

import com.jeeeun.kama.service.cart.model.CartMergeResult;

public record CartMergeResponse(

        int mergedCount

) {
    public static CartMergeResponse from(CartMergeResult result) {
        return new CartMergeResponse(result.mergedCount());
    }
}

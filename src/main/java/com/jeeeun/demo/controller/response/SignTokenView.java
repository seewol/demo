package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.auth.model.SignTokenResult;

// Controller 응답 DTO (Response)
public record SignTokenView(
        String accessToken,
        String refreshToken

) {
    public static SignTokenView from(SignTokenResult result) {
        return new SignTokenView(result.accessToken(), result.refreshToken());
    }
}

package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.auth.model.SignTokenResult;

// Controller 응답 DTO (Response)
public record SignTokenView(
        String accessToken

        // Refresh Token → Cookie 안에 심기!

) {
    public static SignTokenView from(SignTokenResult result) {
        return new SignTokenView(result.accessToken());
    }
}

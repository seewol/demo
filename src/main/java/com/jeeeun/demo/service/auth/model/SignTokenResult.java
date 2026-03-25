package com.jeeeun.demo.service.auth.model;

public record SignTokenResult (
        String accessToken,
        String refreshToken

) {
    public static SignTokenResult from(String accessToken, String refreshToken) {

        return new SignTokenResult(accessToken, refreshToken);
    }
}
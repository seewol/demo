package com.jeeeun.kama.service.auth.model;

public record SignTokenResult (

        String accessToken,
        String refreshToken

) {
    public static SignTokenResult from(String accessToken, String refreshToken) {

        return new SignTokenResult(accessToken, refreshToken);
    }
}
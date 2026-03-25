package com.jeeeun.demo.service.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// 구글 응답 JSON 키 이름이 snake_case 라서
// @JsonProperty로 자바의 camelCase와 매핑해줌.
public record GoogleTokenResponse(


        // 구글 API 호출 시, 인증된 사용자임을 증명하는 토큰
        @JsonProperty("access_token")
        String accessToken,

        // accessToken 언제 만료되는지 (초)
        @JsonProperty("expires_in")
        int expiresIn,

        // 토큰 범위 (email, name, profile 등)
        String scope,

        // 토큰 종류 (보통은 "Bearer")
        @JsonProperty("token_type")
        String tokenType,

        // 사용자 신원 정보 담은 JWT (이메일, 이름 등 포함)
        @JsonProperty("id_token")
        String idToken


) {
}

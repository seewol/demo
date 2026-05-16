package com.jeeeun.demo.service.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// 포트원 서버가 액세스 토큰 발급 요청에 응답하는 'JSON → 자바 객체'로 변환
// 응답 구조: { "code": 0, "response": { "access_token": ".." } }
public record PortOneTokenResponse(

        @JsonProperty("response")
        PortOneTokenBody response

) {

    public record PortOneTokenBody(

            // JSON 키는 snake_case라 @JsonProperty로 camelCase 필드에 매핑!
            @JsonProperty("access_token")
            String accessToken

    ) {}
}

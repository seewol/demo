package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleSignInRequest(

        @NotBlank(message = "인가 코드는 필수입니다.")
        String code,

        // 구글 콘솔에 등록한 주소
        // http://localhost:8080/auth/sign-in/google
        @NotBlank(message = "리디렉션 URI는 필수입니다.")
        String redirectUri
) {
}

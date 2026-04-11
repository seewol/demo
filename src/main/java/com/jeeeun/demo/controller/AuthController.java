package com.jeeeun.demo.controller;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.controller.request.GoogleSignInRequest;
import com.jeeeun.demo.controller.request.LocalSignInRequest;
import com.jeeeun.demo.controller.response.SignTokenView;
import com.jeeeun.demo.service.AuthService;
import com.jeeeun.demo.service.auth.model.SignTokenResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로컬 로그인", description = "이메일, 비밀번호로 로그인 후 JWT 발급")
    @PostMapping("/auth/sign-in")
    public SignTokenView signIn(
            @Valid @RequestBody LocalSignInRequest request,
            HttpServletResponse response    // 쿠키 쓰려면 필요
    ) {

        SignTokenResult result = authService.signIn(request.toCommand());
        response.addCookie(createRefreshTokenCookie(result.refreshToken()));

        return SignTokenView.from(result);
    }


    @Operation(summary = "구글 소셜 로그인", description = "Google OAuth2 access token으로 로그인/회원가입을 처리하고 JWT를 발급")
    @PostMapping("/auth/sign-in/google")
    public SignTokenView googleSignIn(
            @Valid @RequestBody GoogleSignInRequest request,
            HttpServletResponse response
    ) {

        SignTokenResult result = authService.googleOAuth2(request.code(), request.redirectUri());
        response.addCookie(createRefreshTokenCookie(result.refreshToken()));

        return SignTokenView.from(result);
    }


    // 쿠키 생성용 메서드
    private Cookie createRefreshTokenCookie(String refreshToken) {

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);   // JS 접근 차단 (XSS 방어)
        cookie.setSecure(false);    // 로컬 HTTP 환경 (* 배포 시 true로 변경)
        cookie.setPath("/");        // 모든 경로로부터 전송
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7일 (초 단위)

        return cookie;
    }


    @Operation(summary = "토큰 재발급", description = "Refresh Token 쿠키로 새 Access Token 발급")
    @PostMapping("/auth/refresh")
    public SignTokenView refresh(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        // null 방어
        if (cookies == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // todo 1 : 쿠키에서 refreshToken 꺼내기
        String refreshToken = Arrays.stream(cookies) // Cookie[]
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()    // 필터링된 것 중 첫 번째 꺼냄. Optional<Cookie> 타입
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN))
                .getValue();    // 쿠키 객체 안의 실제 토큰 문자열 꺼냄.

        // Cookie 객체 = { name: "refreshToken", value: "eyJhbGEI..." }
        // getValue() → "eyJhbGEI..."

        // todo 2 : 새로운 accessToken 발급
        String accessToken = authService.refresh(refreshToken);

        // todo 3 : 응답하기
        return new SignTokenView(accessToken);
    }

}


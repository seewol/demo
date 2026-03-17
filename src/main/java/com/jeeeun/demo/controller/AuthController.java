package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.LocalSignInRequest;
import com.jeeeun.demo.controller.request.SignInRequest;
import com.jeeeun.demo.controller.response.SignTokenView;
import com.jeeeun.demo.service.AuthService;
import com.jeeeun.demo.service.auth.model.SignTokenResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로컬 로그인", description = "이메일, 비밀번호로 로그인 후 JWT 발급")
    @PostMapping("/auth/sign-in")
    public SignTokenView signIn(
            @Valid @RequestBody LocalSignInRequest request
    ) {
        SignTokenResult result = authService.signIn(request.toCommand());
        return SignTokenView.from(result);
    }

//    @Operation(summary = "구글 소셜 로그인", description = "Google OAuth2 access token으로 로그인/회원가입을 처리하고 JWT를 발급")
//    @PostMapping("/sign/{provider}")
//    public SignTokenView googleSignIn(
//            @PathVariable("provider") String provider,  // "google"
//            @RequestBody SignInRequest request
//    ) {
//        // ① GOOGLE만 허용
//        if (!provider.equalsIgnoreCase("google")) {
//            throw new IllegalArgumentException("sign in provider must be GOOGLE");
//        }
//
//        return SignTokenView.from(authService.googleOAuth2(request.accessToken())); // googleOAuth2
//    }
}

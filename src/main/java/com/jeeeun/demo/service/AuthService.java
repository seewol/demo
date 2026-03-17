package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.controller.response.SignTokenView;
import com.jeeeun.demo.domain.user.Provider;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.domain.user.UserCredentials;
import com.jeeeun.demo.external.google.GoogleOAuth2Client;
import com.jeeeun.demo.repository.UserCredentialsRepository;
import com.jeeeun.demo.repository.UserRepository;
import com.jeeeun.demo.service.auth.model.SignTokenResult;
import com.jeeeun.demo.service.auth.model.UserSignInCommand;
import com.jeeeun.demo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
//    private final GoogleOAuth2Client googleOAuth2Client;


//    @Transactional
//    public SignTokenResult googleOAuth2(String accessToken) {
//        // ② Google API로 사용자 정보 조회
//        // ③ 기존 유저 조회 or 신규 생성 (가입/로그인 통합)
//        // ④ 기존 Credentials 조회 or 신규 생성
//        // ⑤ DB 저장
//        // ⑥ JWT 발급
//    }

    // 로컬 로그인
    @Transactional
    public SignTokenResult signIn(UserSignInCommand command) {

        // 1. 이메일 + LOCAL 방식으로 인증 정보 조회
        // UserCredentials 안에 User가 연결되어 있다.
        UserCredentials credentials = userCredentialsRepository
                .findByIdentifierAndProvider(command.email(), Provider.LOCAL)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // 2. 탈퇴한 회원 여부 확인
        if (credentials.getUser().isDeleted()) {
            throw new BusinessException(ErrorCode.WITHDRAWN_USER);
        }

        // 3. 비밀번호 검증
        // passwordEncoder.matches(입력 비밀번호, DB에 저장된 암호화 비밀번호)
        if (!passwordEncoder.matches(command.password(), credentials.getSecret())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 4. JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(credentials.getUser());
        String refreshToken = jwtTokenProvider.createRefreshToken(credentials.getUser());

        // 5. 토큰 반환
        return SignTokenResult.from(accessToken, refreshToken);
    }

}
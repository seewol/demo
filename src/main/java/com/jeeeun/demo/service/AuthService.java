package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.user.Provider;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.domain.user.UserCredentials;
import com.jeeeun.demo.external.google.GoogleOAuth2Client;
import com.jeeeun.demo.repository.UserCredentialsRepository;
import com.jeeeun.demo.repository.UserRepository;
import com.jeeeun.demo.service.auth.model.GoogleTokenResponse;
import com.jeeeun.demo.service.auth.model.SignTokenResult;
import com.jeeeun.demo.service.auth.model.UserSignInCommand;
import com.jeeeun.demo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final GoogleOAuth2Client googleOAuth2Client;


    // 구글 소셜 로그인 (회원가입 + 로그인 통합)
    @Transactional
    public SignTokenResult googleOAuth2(String authorizationCode, String redirectUri) {
        // todo 1 : 인가 코드로 구글에서 Access Token 받아오기
        GoogleTokenResponse tokenResponse
                = googleOAuth2Client.getAccessToken(authorizationCode, redirectUri);

        // todo 2 : Access Token으로 구글 사용자 정보(id, email, name) 조회
        Map<String, Object> userInfo
                = googleOAuth2Client.getUserInfo(tokenResponse.accessToken());

        String googleId = String.valueOf(userInfo.get("id"));   // 구글 계정의 고유 ID (숫자 문자열 → "123456")
        String email = (String) userInfo.get("email");          // 구글 계정 이메일
        String name = (String) userInfo.get("name");            // 구글 계정에 설정된 이름

        // todo 3 : 구글 ID로 이미 가입한 회원인지 확인하기
        // 구글 ID + GOOGLE PROVIDER 검색!
        Optional<UserCredentials> existingCredentials
                = userCredentialsRepository.findByIdentifierAndProvider(googleId, Provider.GOOGLE);

        User user;

        if (existingCredentials.isPresent()) {
            // todo 3-1 : 가입 O → 탈퇴 여부 확인 후 기존 회원 로그인
            user = existingCredentials.get().getUser();

            if (user.isDeleted()) {
                throw new BusinessException(ErrorCode.WITHDRAWN_USER);
            }

        } else {
            // todo 3-2 : 가입 X → 새로 회원가입 후 로그인
            // User, UserCredentials 같이 생성
            user = User.builder()
                    .email(email)
                    .name(name)
                    .build();
            userRepository.save(user);

            // 구글 로그인은 비밀번호 없음 : secret = null
            UserCredentials credentials = UserCredentials.builder()
                    .user(user)
                    .provider(Provider.GOOGLE)
                    .identifier(googleId)   // 구글 고유 ID를 identifier 저장
                    .secret(null)           // 소셜 로그인은 우리 DB에 비밀번호 없음
                    .build();

            userCredentialsRepository.save(credentials);
        }

        // todo 4 : JWT 토큰 발급 후 리턴
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        return SignTokenResult.from(accessToken, refreshToken);

    }


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
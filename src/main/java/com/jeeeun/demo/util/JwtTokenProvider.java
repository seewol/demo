package com.jeeeun.demo.util;

import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.util.exception.InvalidTokenException;
import com.jeeeun.demo.util.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

// @Component : Spring Bean 으로 등록하는 어노테이션
// 1. Spring이 알아서 JwtTokenProvider 객체 딱! 1개 생성
// 2. @Value로 yml 값 주입
// 3. 필요한 곳에 자동으로 넣어줌 (DI : 의존관계 주입)

// Dependency Injection : 필요한 객체는 직접 만들지 말고 Spring 한테 달라고 해!
// @Component : 나 Spring한테 관리 받을게.
// @RequiredArgsConstructor : final 필드에 Spring이 넣어줘.

@Slf4j  // Lombok 로깅 어노테이션
@Component // Spring Bean 으로 등록하는 어노테이션
public class JwtTokenProvider {

    // JWT 서명에 사용하는 암호화 키
    // String 아닌 SecretKey 타입 : jjwt 라이브러리가 요구하는 형식!
    private final SecretKey secretKey;

    // 액세스 토큰 만료 시간
    private final long accessTokenValidity;

    // 리프레시 토큰 만료 시간
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            // applicaion.yml 정보를 가져옴
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity:1800000}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity:604800000}") long refreshTokenValidity) {

        // 최소 256비트(32바이트) 키 필요
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // ▼ 토큰 생성

    public String createAccessToken(User user) {
        return createToken(user, accessTokenValidity, "ACCESS");
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshTokenValidity, "REFRESH");
    }

    private String createToken(User user, long validity, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                // Registered Claims (표준 클레임)
                .issuer("flab.com")                       // 발급자
                .subject(String.valueOf(user.getId()))       // 토큰 주체 (누구 토큰인지 사용자 식별)
                .issuedAt(now)                               // 발급 시간
                .expiration(expiry)                          // 만료 시간
                // 해당 시간 지나면 parseToken() 에서 ExpiredJwtException 터짐
                .id(UUID.randomUUID().toString())            // 토큰 고유 ID (jti)

                // Private Claims (내가 정의한 커스텀 클레임)
                .claim("userEmail", user.getEmail())
                // .claim("userType", user.getUserType().name())
                .claim("tokenType", tokenType)
                // "ACCESS", "REFRESH" 구분용

                // 서명 (토큰 위조 여부 검증 가능)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact(); // 위 설정들 전부 조합해 최종 JWT 문자열 생성
    }

    // ▼ 토큰 검증 & 유틸

    // Claims = 토큰 내 저장된 데이터 덩어리 (subject, email, tokenType 등)
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)      // 서명 검증에 사용할 키 지정
                    .build()
                    .parseSignedClaims(token)   // 실제 파싱 + 검증
                    .getPayload();              // Claims 꺼내기

        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료: {}", e.getMessage()); // {} = 로그 메세지 안에 변수 넣는법
            throw new TokenExpiredException("토큰이 만료되었습니다.");

        } catch (JwtException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }


    // ✅ 유틸리티 메서드 추가

    // ▼ JwtAuthenticationFilter 안에서 토큰 유효성 체크할 때 사용
    public boolean isValid(String token) {
        try {
            parseToken(token);  // 파싱 성공 시
            return true;        // 유효한 토큰
        } catch (Exception e) {
            return false;       // 유효하지 않은 토큰
        }
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
        // parseToken() : Claims 꺼내기
        // .getSubject() : sub 클레임 꺼내기 (String 타입 userId!)
        // Long.parseLong() : String → Long 타입 변환
        // CreateToken() 에서 String.valueOf(user.getIt())로 저장했던 거 반대로 변환
    }

}

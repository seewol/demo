package com.jeeeun.demo.util;

import com.jeeeun.demo.domain.member.Member;
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

@Slf4j  // Lombok의 로깅 어노테이션
@Component // <- Spring Bean 으로 등록 -> 어디서든 주입받아서 사용 가능
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidity;
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

    /**
     * Access Token 생성
     * - 짧은 수명
     * - 사용자 인증 정보 포함
     */
    public String createAccessToken(Member user) {
        return createToken(user, accessTokenValidity, "ACCESS");
    }

    private String createToken(Member user, long validity, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                // Registered Claims (표준 클레임)
                .issuer("flab.com")                          // 발급자
                .subject(String.valueOf(user.getId()))       // 주체 (사용자 식별자)
                .issuedAt(now)                               // 발급 시간
                .expiration(expiry)                          // 만료 시간
                .id(UUID.randomUUID().toString())            // 토큰 고유 ID (jti)

                // Private Claims (커스텀 클레임)
                .claim("userEmail", user.getEmail())
                // .claim("userType", user.getUserType().name())
                .claim("tokenType", tokenType)

                // 서명
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료: {}", e.getMessage());
            throw new TokenExpiredException("토큰이 만료되었습니다.");

        } catch (JwtException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    // ✅ 유틸리티 메서드 추가
    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }
}

package com.jeeeun.demo.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// @Slf4j
// Lombok 로깅 어노테이션 (실무에서는 println 대신 씀)
// ex. log.info("요청 들어옴"), log.warn("토큰 만료"), log.error("오류")
@Slf4j
@RequiredArgsConstructor // final 붙은 필드 골라 생성자 자동으로 만들어줌
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰 검증을 중복으로 하면 안 되기 때문에 OncePerRequestFilter 상속 (요청 1 ↔ 실행 1 보장)

    private final JwtTokenProvider jwtTokenProvider;

    // ✅ doFilterInternal 오버라이드 이유?
    // OncePerRequestFilter 가 '1번 실행'을 보장하고,
    // '실제 필터 로직은' doFilterInternal 에 작성하도록 설계되어 있음.
    // 즉! 부모 클래스가 중복 실행 방지를 처리하므로, 여긴 '토큰 검증 로직'만

    @Override
    protected void doFilterInternal(HttpServletRequest request, // 클라이언트가 보낸 요청
                                    HttpServletResponse response, // 서버가 보낼 응답
                                    FilterChain filterChain) // 다음 필터로 넘기는 체인
            throws ServletException, IOException {

        // 1. 헤더에서 토큰 꺼내기 (요청 시 클라이언트가 헤더에 토큰 담아 보냄)
        // ex. Authorization: Bearer eyJhbGci...
        String token = resolveToken(request);

        // 2. 토큰이 있고, 유효하면 SecurityContext에 인증 정보 저장
        if (StringUtils.hasText(token) && jwtTokenProvider.isValid(token)) {

            Long userId = jwtTokenProvider.getUserId(token);
            String role = jwtTokenProvider.getRole(token);

            // Spring Security 에게 '해당 사용자가 인증된 사용자'임을 알려주는 객체
            // 이름이 Username,Password 인데 JWT 방식애서도 그냥 이 객체를 사용함
            // (Spring Security 내부 표준 객체이기 때문)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,     // principal (현재 로그인한 사용자 식별자)
                            null,       // credentials (비밀번호 → 토큰 방식은 불필요)
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))   // authorities *권한 목록)
                            // Spring Security 내부적으로 "ROLE_" 붙은 것을 권한으로 인식
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터로 넘겨!
        filterChain.doFilter(request, response);
    }

    // 토큰을 가진 사람(bearer)
    // "Bearer ..." : "..." 앞의 Bearer 제거
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /*
        Authorization: Bearer eyJhbGci...
          ↑               ↑
       헤더 이름        값 (타입 + 토큰)
    */

}

package com.jeeeun.demo.common.config;

import com.jeeeun.demo.util.JwtAuthenticationFilter;
import com.jeeeun.demo.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // 설정 클래스임을 Spring에게 알리는 역할 (내부 @Bean 메서드를 읽어 Bean으로 등록해줌)
@EnableWebSecurity  // Spring Security 활성화 (이거 없음 아래 설정 동작 X)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // ▼ CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // CSRF = 타 사이트에서우리 서버로 요청을 위조하는 공격
                // 브라우저 기반 (세션, 쿠키)일 때 필요 → 난 JWT 방식이라 토큰 인증이라 필요 X (끄기)

                // ▼ 세션 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // STATELESS = 세션 안 씀 (JWT가 세션 역할 대신해서 서버가 로그인 상태 기억할 필요 X)

                // ▼ URL별 접근 권한 설정
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                "/auth/sign-in",    // 로컬 로그인
                                "/auth/sign-in/**", // 소셜 로그인
                                "/auth/refresh",    // 토큰 재발급
                                "/sign-up",         // 회원가입
                                "/swagger-ui/**",   // Swagger UI
                                "/v3/api-docs/**"   // Swagger API 문서
                            ).permitAll()   // 위 URL 들은 토큰 없어도 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // ADMIN만 접근 가능
                        .anyRequest().authenticated()   // 나머지 모든 요청은 토큰 필요!
                )

                .exceptionHandling(exception ->
                        exception   // 토큰 없거나 만료 = 401
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.getWriter().write("{\"message\": \"로그인이 필요합니다.\"}");
                                })  // 토큰 있어도 권한 없음 = 403
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.getWriter().write("{\"message\": \"접근 권한이 없습니다.\"}");
                                })
                )

                // ▼ JWT 필터 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class // 객체가 아닌 클래스 정보
                );
                // UsernamePasswordAuthenticationFilter = Spring Security 기본 필터인데
                // 그 필터 앞에 내 JWT 필터를 끼워 넣으면서,
                // 요청이 오면 JWT 필터가 먼저 토큰 확인한 후 다음으로 넘김.
                // JWT 필터 → UsernamePasswordAuthenticationFilter → ... → Controller

        return httpSecurity.build();

    }

}

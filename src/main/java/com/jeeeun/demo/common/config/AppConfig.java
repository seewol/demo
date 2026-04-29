package com.jeeeun.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableScheduling   // ★ 스케줄러 사용을 Spring에 선언 → Spring이 @Scheduled 붙은 메서드 전부 스캔해 등록!
public class AppConfig {

    // ▼ 여기서 Bean 등록해야 AuthService, UserCommandService 등에 주입받아 쓸 수 있음.
    // DI 때문! → 아니면 각 클래스마다 계쏙 생성하므로 메모리 낭비
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // BCrypt = 단방향 해시 암호화 알고리즘
        // 같은 비밀번호도 매번 다른 해시값을 생성한다. (salt 자동 적용)
        // 복호화가 불가하기 때문에, 원본 비밀번호를 알 수 없다!
        // passwordEncoder.matches() 이용해 검증만 가능.
    }

    // 내가 만든 클래스  → @Component 달면 됨 (Spring이 알아서 Bean 등록)
    // 외부 라이브러리   → @Bean으로 직접 등록해야 함 (내가 직접 객체 만들어서 Spring에게 관리 맡김)
    // 외부 라이브러리 클래스는 @Component 못 달아서 @Bean으로 등록!
}

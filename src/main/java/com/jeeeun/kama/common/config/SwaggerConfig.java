package com.jeeeun.kama.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Swagger UI 우측 상단 Authorize 버튼에서 쓸 인증 방식 이름
    private static final String JWT_SCHEME_NAME = "JWT";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Kama API Document")
                .version("v0.0.1")
                .description("""
                        카마(Kama) 이커머스 백엔드 API 명세서입니다.

                        [공통 응답 안내]
                        - 로그인이 필요한 API에 토큰 없이 접근 시 401 Unauthorized
                        - 권한이 없는 API(예: 관리자 전용)에 접근 시 403 Forbidden
                        - 위 두 응답은 모든 API에 공통으로 적용되어 API별로 별도 표기하지 않습니다.
                        - API별 404 / 400 / 409 등은 각 API 설명에 필요한 경우에만 표기했습니다.

                        [인증 방법]
                        - 우측 상단 Authorize 버튼을 눌러 Access Token 값만 입력하세요.
                        - "Bearer " 접두사는 Swagger UI가 자동으로 붙여 따로 입력 안 함
                        """);

        // JWT를 Authorization 헤더의 Bearer 토큰으로 받는다는 걸 Swagger에게 알려주는 설정
        // 실 인증 로직은 SecurityConfig에 있고 이건 설명용 메타 데이터
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT_SCHEME_NAME);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(JWT_SCHEME_NAME, jwtScheme))
                .info(info)
                .addSecurityItem(securityRequirement);
    }

}

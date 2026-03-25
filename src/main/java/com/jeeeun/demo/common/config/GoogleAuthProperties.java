package com.jeeeun.demo.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "google")
// yml 에서 "google: " 아래 값을 자동 매핑해줌
// ex) google.client-id 는 clientId 필드에 바인딩
public class GoogleAuthProperties {

    private final String clientId;
    private final String clientSecret;

}

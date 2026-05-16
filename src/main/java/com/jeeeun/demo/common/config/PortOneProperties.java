package com.jeeeun.demo.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "port-one")
public class PortOneProperties {

    private final String apiKey;        // port-one.api-key
    private final String apiSecret;     // port-one.api-secret
    private final String impCode;       // port-one.imp-code

}

package com.jeeeun.demo.service.auth.model;

import lombok.Builder;

@Builder
public record UserSignInCommand(
    String email,
    String password
) {
}

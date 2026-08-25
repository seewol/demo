package com.jeeeun.kama.service.auth.model;

import lombok.Builder;

@Builder
public record UserSignInCommand(
    String email,
    String password
) {
}

package com.jeeeun.demo.service.member.model;

import lombok.Builder;

@Builder
public record UserLoginCommand(
    String email,
    String password
) {
}

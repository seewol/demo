package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.member.model.UserLoginCommand;
import lombok.Builder;

@Builder
public record UserLoginRequest (
    String email,
    String password
) {
    public UserLoginCommand toCommand() {
        return UserLoginCommand.builder()
                .email(email)
                .password(password)
                .build();
    }
}


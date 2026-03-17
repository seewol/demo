package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.auth.model.UserSignInCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LocalSignInRequest(
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    String email,
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    String password

) {
    public UserSignInCommand toCommand() {
        return UserSignInCommand.builder()
                .email(email)
                .password(password)
                .build();
    }
}


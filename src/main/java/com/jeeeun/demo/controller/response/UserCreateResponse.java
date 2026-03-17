package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.user.model.UserCreateResult;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public record UserCreateResponse(
    Long id,
    String name,
    String email,
    String phoneNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
    public static UserCreateResponse from(UserCreateResult result) {
        return UserCreateResponse.builder()
                .id(result.id())
                .name(result.name())
                .email(result.email())
                .phoneNumber(result.phoneNumber())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();
    }

    // Swagger 용 예시 데이터
    public static UserCreateResponse example() {
        return UserCreateResponse.builder()
                .id(1L)
                .name("테스트")
                .email("test@gmail.com")
                .phoneNumber("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
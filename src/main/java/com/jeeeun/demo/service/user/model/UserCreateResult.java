package com.jeeeun.demo.service.user.model;

import com.jeeeun.demo.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserCreateResult(
    Long id,
    String name,
    String email,
    String phoneNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
    public static UserCreateResult from(User User) {
        return UserCreateResult.builder()
                .id(User.getId())
                .name(User.getName())
                .email(User.getEmail())
                .phoneNumber(User.getPhoneNumber())
                .createdAt(User.getCreatedAt())
                .updatedAt(User.getUpdatedAt())
                .build();
    }
}

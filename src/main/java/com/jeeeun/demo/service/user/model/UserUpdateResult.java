package com.jeeeun.demo.service.user.model;

import com.jeeeun.demo.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserUpdateResult(

        Long userId,
        String email,
        String name,
        String phoneNumber,
        LocalDateTime updatedAt

) {
    public static UserUpdateResult from(User user) {
        return UserUpdateResult.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
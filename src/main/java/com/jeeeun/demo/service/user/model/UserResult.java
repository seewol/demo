package com.jeeeun.demo.service.user.model;

import com.jeeeun.demo.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResult(
        Long id,
        String name,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    // Entity → Result 변환은 Result 에서 static from(Entity)로 처리

    public static UserResult from(User user) {
        return UserResult.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}

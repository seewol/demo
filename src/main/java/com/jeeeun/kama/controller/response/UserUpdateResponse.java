package com.jeeeun.kama.controller.response;

import com.jeeeun.kama.service.user.model.UserUpdateResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserUpdateResponse(
    Long id,
    String email,
    String name,
    String phoneNumber,
    LocalDateTime updatedAt

) {

    public static UserUpdateResponse from(UserUpdateResult result) {
        return UserUpdateResponse.builder()
                .id(result.userId())
                .email(result.email())
                .name(result.name())
                .phoneNumber(result.phoneNumber())
                .updatedAt(result.updatedAt())
                .build();
    }

}

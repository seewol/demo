package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.user.model.UserUpdateResult;
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
                .id(result.id())
                .email(result.email())
                .name(result.name())
                .phoneNumber(result.phoneNumber())
                .updatedAt(result.updatedAt())
                .build();
    }

}

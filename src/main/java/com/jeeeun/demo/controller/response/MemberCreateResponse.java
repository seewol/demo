package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.member.model.MemberCreateResult;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public record MemberCreateResponse (
    Integer id,
    String name,
    String email,
    String phoneNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
    public static MemberCreateResponse from(MemberCreateResult result) {
        return MemberCreateResponse.builder()
                .id(result.id())
                .name(result.name())
                .email(result.email())
                .phoneNumber(result.phoneNumber())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();
    }

    // Swagger 용 예시 데이터
    public static MemberCreateResponse example() {
        return MemberCreateResponse.builder()
                .id(1)
                .name("테스트")
                .email("test@gmail.com")
                .phoneNumber("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
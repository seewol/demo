package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.member.model.MemberResult;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public record MemberResponse (

    Integer id,
    String name,
    String email,
    String phoneNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

    // Result → Response 변환은 Response 에서 static from(Result) 로 처리
    public static MemberResponse from(MemberResult result) {
        return MemberResponse.builder()
                .id(result.id())
                .name(result.name())
                .email(result.email())
                .phoneNumber(result.phoneNumber())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();
    }
}

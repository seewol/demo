package com.jeeeun.demo.service.member.model;

import com.jeeeun.demo.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberResult (
        Integer id,
        String name,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    // Entity → Result 변환은 Result 에서 static from(Entity)로 처리

    public static MemberResult from(Member member) {
        return MemberResult.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

}

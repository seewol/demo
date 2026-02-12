package com.jeeeun.demo.service.member.model;

import com.jeeeun.demo.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberCreateResult (
    Integer id,
    String name,
    String email,
    String phoneNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
    public static MemberCreateResult from(Member member) {
        return MemberCreateResult.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}

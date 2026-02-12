package com.jeeeun.demo.service.member.model;

import com.jeeeun.demo.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberUpdateResult (

        Integer id,
        String email,
        String name,
        String phoneNumber,
        LocalDateTime updatedAt

) {
    public static MemberUpdateResult from(Member member) {
        return MemberUpdateResult.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.service.member.model.MemberUpdateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record MemberUpdateResponse (
    Integer id,
    String email,
    String name,
    String phoneNumber,
    LocalDateTime updatedAt

) {

    public static MemberUpdateResponse from(MemberUpdateResult result) {
        return MemberUpdateResponse.builder()
                .id(result.id())
                .email(result.email())
                .name(result.name())
                .phoneNumber(result.phoneNumber())
                .updatedAt(result.updatedAt())
                .build();
    }

}

package com.jeeeun.demo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateResponse {

    private Integer memberId;
    private String memberEmail;
    private String memberName;
    private String phoneNumber;
    private LocalDateTime updatedAt;
}

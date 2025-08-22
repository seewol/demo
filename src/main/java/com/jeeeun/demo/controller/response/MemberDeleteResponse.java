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
public class MemberDeleteResponse {
    private Integer memberId;
    private boolean isDeleted;
    private LocalDateTime deletedAt;
}

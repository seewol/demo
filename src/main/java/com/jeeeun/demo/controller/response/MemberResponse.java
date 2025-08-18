package com.jeeeun.demo.controller.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// 프론트에 내려주는 응답 형태를 정의한 클래스
public class MemberResponse {

    Integer memberId;
    String memberName;
    String memberEmail;
    String phoneNumber;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

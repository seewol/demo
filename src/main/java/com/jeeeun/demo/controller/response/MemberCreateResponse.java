package com.jeeeun.demo.controller.response;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberCreateResponse {
    private Integer memberId;
    private String memberName;
    private String memberEmail;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Swagger 용 예시 데이터
    public static MemberCreateResponse example() {
        return MemberCreateResponse.builder()
                .memberId(1)
                .memberName("테스트")
                .memberEmail("test@gmail.com")
                .phoneNumber("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
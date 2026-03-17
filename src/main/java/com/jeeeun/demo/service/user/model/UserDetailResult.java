package com.jeeeun.demo.service.user.model;

import com.jeeeun.demo.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDetailResult(
        Long id,
        String name,
        String email,
        String phoneNumber,
        ProductSummaryResult productSummary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    // ★ 회원마다 대표 상품이 있을 수도, 없을 수도 있음

    // 상품 요약을 제외한 내 정보 조회
    public static UserDetailResult from(User user) {
        return UserDetailResult.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .productSummary(null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // * 주 사용 * 상품 요약을 포함한 내 정보 조회
    public static UserDetailResult from(
            User user, ProductSummaryResult productSummaryResult) {
        return UserDetailResult.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .productSummary(productSummaryResult)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

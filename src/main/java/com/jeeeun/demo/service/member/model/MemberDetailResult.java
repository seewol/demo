package com.jeeeun.demo.service.member.model;

import com.jeeeun.demo.domain.member.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberDetailResult (
        Integer id,
        String name,
        String email,
        String phoneNumber,
        ProductSummaryResult productSummary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    // ★ 회원마다 대표 상품이 있을 수도, 없을 수도 있음

    // 상품 요약을 제외한 내 정보 조회
    public static MemberDetailResult from(Member member) {
        return MemberDetailResult.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .productSummary(null)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    // * 주 사용 * 상품 요약을 포함한 내 정보 조회
    public static MemberDetailResult from(
            Member member, ProductSummaryResult productSummaryResult) {
        return MemberDetailResult.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .productSummary(productSummaryResult)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}

package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.controller.response.MemberDetailResponse;
import com.jeeeun.demo.controller.response.MemberResponse;
import com.jeeeun.demo.domain.Member;
import com.jeeeun.demo.domain.Product;
import com.jeeeun.demo.repository.MemberRepository;
import com.jeeeun.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 상태 변경용 CommandService

@RequiredArgsConstructor
@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 목록 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        return memberRepository.findAllByIsDeletedFalse()
                .stream()
                // map() : Member 엔티티 → MemberResponse DTO로 변환
                .map(member -> MemberResponse.builder()
                        .memberId(member.getMemberId())
                        .memberName(member.getMemberName())
                        .memberEmail(member.getMemberEmail())
                        .phoneNumber(member.getPhoneNumber())
                        .createdAt(member.getCreatedAt())
                        .updatedAt(member.getUpdatedAt())
                        .build())
                .toList();
    }

    // 회원 단일 조회에 대한 api 생성 (내 정보 조회)
    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberDetail(Integer memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        return MemberDetailResponse.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberEmail(member.getMemberEmail())
                .phoneNumber(member.getPhoneNumber())
                .productSummary(null)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
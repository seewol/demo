package com.jeeeun.demo.service;

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

@RequiredArgsConstructor
@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    // 전체 멤버 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        return memberRepository.findAll()
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

    // 회원 상세 조회
    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberDetail(Integer memberId, Integer productId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        return MemberDetailResponse.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberEmail(member.getMemberEmail())
                .phoneNumber(member.getPhoneNumber())
                .product(
                    MemberDetailResponse.ProductDetailResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .build()
                )
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
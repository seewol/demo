package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.member.Member;
import com.jeeeun.demo.repository.MemberRepository;
import com.jeeeun.demo.repository.ProductRepository;
import com.jeeeun.demo.service.member.model.MemberDetailResult;
import com.jeeeun.demo.service.member.model.MemberResult;
import com.jeeeun.demo.service.member.model.ProductSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 조회 전용 QueryService

@RequiredArgsConstructor
@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 회원 목록 조회에 대한 api 생성
    @Transactional(readOnly = true)
    public List<MemberResult> getMembers() {
        return memberRepository.findAllByIsDeletedFalse()
                .stream()
                // map() : Member 엔티티 → MemberResult 로 변환
                .map(MemberResult::from)
                // member -> MemberResult.from(member) 와 동일
                // MemberResult 안의 from 메서드를 함수로 사용한다는 의미
                .toList();
    }

    // 회원 단일 조회에 대한 api 생성 (내 정보 조회)
    @Transactional(readOnly = true)
    public MemberDetailResult getMemberDetail(Integer memberId) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        ProductSummaryResult ps = productRepository.findMainSummaryByMemberId(memberId)
                .orElse(null); // 상품 없으면 ps 가 null

        return MemberDetailResult.from(member, ps);
    }
}
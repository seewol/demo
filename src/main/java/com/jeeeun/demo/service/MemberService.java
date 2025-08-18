package com.jeeeun.demo.service;

import com.jeeeun.demo.controller.request.MemberCreateRequest;
import com.jeeeun.demo.controller.request.MemberUpdateRequest;
import com.jeeeun.demo.controller.response.MemberCreateResponse;
import com.jeeeun.demo.controller.response.MemberResponse;
import com.jeeeun.demo.controller.response.MemberUpdateResponse;
import com.jeeeun.demo.domain.member.Member;
import com.jeeeun.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.util.StringUtils.hasText;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    // 회원가입에 대한 api 생성
    @Transactional // Read 빼고는 @Transactional 붙여주기.
    public MemberCreateResponse createMember(MemberCreateRequest req) {
        // 고려 사항. email 중복 체크

        Member saved = memberRepository.save(
                Member.builder()
                        .memberName(req.getMemberName())
                        .memberEmail(req.getMemberEmail())
                        .memberPw(passwordEncoder.encode(req.getMemberPw()))
                        .phoneNumber(req.getPhoneNumber())
                        .build()
        );

        return MemberCreateResponse.builder()
                .memberId(saved.getMemberId())
                .memberName(saved.getMemberName())
                .memberEmail(saved.getMemberEmail())
                .phoneNumber(saved.getPhoneNumber())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }


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


    // 내 정보 수정에 대한 api 생성
    @Transactional
    public MemberUpdateResponse updateMember(Integer memberId, MemberUpdateRequest req) {


        // 1) 수정 대상 조회
        Member updated = memberRepository.findById(memberId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 없습니다."));

        // 2) 수정 내용 필드 반영
        if (hasText(req.getMemberName())) {
            updated.setMemberName(req.getMemberName().trim());
        }

        if (hasText(req.getPhoneNumber())) {
            updated.setPhoneNumber(req.getPhoneNumber().trim());
        }

        // Auditing의 @LastModifiedDate 반영 보장!
        memberRepository.saveAndFlush(updated);

        return MemberUpdateResponse.builder()
                .memberId(updated.getMemberId())
                .memberEmail(updated.getMemberEmail())
                .memberName(updated.getMemberName())
                .phoneNumber(updated.getPhoneNumber())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }

}
/*
    [빌더 패턴에 대한 설명]
    @Builder 어노테이션 달기

    1. 체이닝 방식 : 필드가 많아져도 가독성이 좋음.
    └ 안 그러면 MemberResponse memberResponse = new MemberResponse(id, "name", ..);
    → 생성자에 인자가 많을 때 순서가 헷갈리지 않고, 체이닝 방식으로 읽기 쉽게 객체 생성 가능.

    2. @Builder 어노테이션을 MemberResponse 같은 DTO/Entity 클래스에 붙이면,
       롬복이 빌더 패턴을 자동으로 만들어줌.
 */



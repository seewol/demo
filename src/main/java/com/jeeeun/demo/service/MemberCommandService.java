package com.jeeeun.demo.service;

import com.jeeeun.demo.controller.request.MemberCreateRequest;
import com.jeeeun.demo.controller.request.MemberUpdateRequest;
import com.jeeeun.demo.controller.response.MemberCreateResponse;
import com.jeeeun.demo.controller.response.MemberDeleteResponse;
import com.jeeeun.demo.controller.response.MemberUpdateResponse;
import com.jeeeun.demo.domain.member.Member;
import com.jeeeun.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Service
public class MemberCommandService {

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

    @Transactional
    public MemberDeleteResponse deleteMember(Integer memberId) {

        // 1) 수정 대상 조회 (소프트 딜리트)
        Member deleted = memberRepository.findById(memberId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 없습니다."));

        // 2) 이미 삭제된 경우
        if (deleted.isDeleted()) {
            return MemberDeleteResponse.builder()
                    .memberId(deleted.getMemberId())
                    .isDeleted(true)
                    .deletedAt(deleted.getDeletedAt())
                    .build();
        }

        deleted.setDeleted(true); // set은 is가 빠지는 게 규약이란다.
        deleted.setDeletedAt(LocalDateTime.now());

        memberRepository.save(deleted);

        return MemberDeleteResponse.builder()
                .memberId(deleted.getMemberId())
                .isDeleted(deleted.isDeleted()) // 아래 주석에 설명
                .deletedAt(deleted.getDeletedAt())
                .build();
    }
        /*
            자바 Bean 네이밍 규약에 따르면,
            일반 필드 (String, int 등) 는
            ex) 필드명 : memberName 인 경우,
                getter : getMemberName(), setter : setMemberName(..)
            boolean 필드는 상태를 물어보는 의미로 설계됐기 때문에
            ex) 필드명 :  isDeleted 인 경우,
                getter : isDeleted(), setter : setDeleted(boolean)
         */
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

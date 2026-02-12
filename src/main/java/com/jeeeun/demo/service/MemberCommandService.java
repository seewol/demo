package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.member.Member;
import com.jeeeun.demo.repository.MemberRepository;
import com.jeeeun.demo.service.member.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.hasText;

// 상태 변경용 CommandService

@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입에 대한 api 생성
    @Transactional // Read 빼고는 @Transactional 붙여주기.
    public MemberCreateResult signUp(MemberCreateCommand command) {

        // todo 형식 검사 (validate..)는 Controller 에서 마치고 넘어오기

        // email, phoneNumber 중복 체크 (unique = true)
        if (memberRepository.existsByEmailAndIsDeletedFalse(command.email())
                || memberRepository.existsByPhoneNumberAndIsDeletedFalse(command.phoneNumber())) {
           throw new BusinessException(ErrorCode.CONFLICT_USER);
        }

        try {
            Member saved = memberRepository.save(
                    Member.builder()
                            .name(command.name())
                            .email(command.email())
                            .password(passwordEncoder.encode(command.password()))
                            .phoneNumber(command.phoneNumber())
                            .build()
            );

            return MemberCreateResult.from(saved);

        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.CONFLICT_USER);
        }

//        return MemberCreateResponse.example();
    }


    // 내 정보 수정에 대한 api 생성
    @Transactional
    public MemberUpdateResult updateMember(MemberUpdateCommand command) {

        // 1) 수정 대상 조회
        Member updated = memberRepository.findByIdAndIsDeletedFalse(command.id())
                .orElseThrow( () -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // 2) name 수정
        if (hasText(command.name())) {
            updated.setName(command.name().trim());
        }

        // 3) phoneNumber 수정 (Request.toCommand()에서 정규화/검증 완료)
        if (hasText(command.phoneNumber())) {
            String newPhone = command.phoneNumber().trim();
            String currentPhone = updated.getPhoneNumber();

            // 바뀐 경우에만 중복 체크하기
            if (!newPhone.equals(currentPhone)) {
                boolean exists = memberRepository
                        .existsByPhoneNumberAndIsDeletedFalseAndIdNot(
                                newPhone, updated.getId()
                        ); // 다른 활성 회원이 사용 중이면 막기 (본인 제외)

                if (exists) {
                    throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
                }

                updated.setPhoneNumber(newPhone);
            }
        }

        // Auditing 의 @LastModifiedDate 반영 보장!
        memberRepository.saveAndFlush(updated);
        // updateAt 에 즉시 반영된 값이 필요할 때

        return MemberUpdateResult.from(updated);

    }


    // 회원 탈퇴에 대한 api 생성
    @Transactional
    public void deleteMember(Integer memberId) {

        // 1) 탈퇴 대상 조회 (소프트 딜리트)
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow( () -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        member.setDeleted(true);
        member.setDeletedAt(LocalDateTime.now());
        member.setEmail(null);
        member.setPhoneNumber(null);

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

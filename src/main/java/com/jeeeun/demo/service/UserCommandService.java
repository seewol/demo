package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.user.Provider;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.domain.user.UserCredentials;
import com.jeeeun.demo.repository.UserCredentialsRepository;
import com.jeeeun.demo.repository.UserRepository;
import com.jeeeun.demo.service.user.model.UserCreateCommand;
import com.jeeeun.demo.service.user.model.UserCreateResult;
import com.jeeeun.demo.service.user.model.UserUpdateCommand;
import com.jeeeun.demo.service.user.model.UserUpdateResult;
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
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입에 대한 api 생성
    @Transactional // Read 빼고는 @Transactional 붙여주기.
    public UserCreateResult signUp(UserCreateCommand command) {

        // todo 형식 검사 (validate..)는 Controller 에서 마치고 넘어오기

        // email, phoneNumber 중복 체크 (unique = true)
        if (userRepository.existsByEmailAndIsDeletedFalse(command.email())
                || userRepository.existsByPhoneNumberAndIsDeletedFalse(command.phoneNumber())) {
           throw new BusinessException(ErrorCode.CONFLICT_USER);
        }

        try {
            // 1. User 저장 (비밀번호 제외)
            User saved = userRepository.save(
                    User.builder()
                            .name(command.name())
                            .email(command.email())
                            .phoneNumber(command.phoneNumber())
                            .build()
            );

            // 2. UserCredentials 저장 (비밀번호는 여기!)
            userCredentialsRepository.save(
                    UserCredentials.builder()
                            .user(saved)
                            .provider(Provider.LOCAL)
                            .identifier(command.email())
                            .secret(passwordEncoder.encode(command.password()))
                            .build()
            );

            // UserCredentials 에는 user_id(FK) 가 필요한데,
            // User 를 DB에 저장하면 id를 얻을 수 있겠지?

            return UserCreateResult.from(saved);

        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.CONFLICT_USER);
        }

//        return userCreateResponse.example();
    }


    // 내 정보 수정에 대한 api 생성
    @Transactional
    public UserUpdateResult updateUser(UserUpdateCommand command) {

        // 1) 수정 대상 조회
        User updated = userRepository.findByIdAndIsDeletedFalse(command.id())
                .orElseThrow( () -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // 2) phoneNumber 수정 (Request.toCommand()에서 정규화/검증 완료)
        if (hasText(command.phoneNumber())) {
            String newPhone = command.phoneNumber().trim();
            String currentPhone = updated.getPhoneNumber();

            // 바뀐 경우에만 중복 체크하기
            if (!newPhone.equals(currentPhone)) {
                boolean exists = userRepository
                        .existsByPhoneNumberAndIsDeletedFalseAndIdNot(
                                newPhone, updated.getId()
                        ); // 다른 활성 회원이 사용 중이면 막기 (본인 제외)

                if (exists) {
                    throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
                }
            }
        }

        updated.updateProfile(command.name(), command.phoneNumber());

        // Auditing 의 @LastModifiedDate 반영 보장!
        userRepository.saveAndFlush(updated);
        // updateAt 에 즉시 반영된 값이 필요할 때

        return UserUpdateResult.from(updated);

    }


    // 회원 탈퇴에 대한 api 생성
    @Transactional
    public void deleteUser(Long userId) {

        // 1) 탈퇴 대상 조회 (소프트 딜리트)
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow( () -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        user.withdraw();

        // 2) credentials 삭제
        userCredentialsRepository.deleteByUser(user);
    }

        /*
            자바 Bean 네이밍 규약에 따르면,
            일반 필드 (String, int 등) 는
            ex) 필드명 : userName 인 경우,
                getter : getUserName(), setter : setUserName(..)
            boolean 필드는 상태를 물어보는 의미로 설계됐기 때문에
            ex) 필드명 :  isDeleted 인 경우,
                getter : isDeleted(), setter : setDeleted(boolean)
         */
}

/*
    [빌더 패턴에 대한 설명]
    @Builder 어노테이션 달기

    1. 체이닝 방식 : 필드가 많아져도 가독성이 좋음.
    └ 안 그러면 userResponse userResponse = new userResponse(id, "name", ..);
    → 생성자에 인자가 많을 때 순서가 헷갈리지 않고, 체이닝 방식으로 읽기 쉽게 객체 생성 가능.

    2. @Builder 어노테이션을 userResponse 같은 DTO/Entity 클래스에 붙이면,
       롬복이 빌더 패턴을 자동으로 만들어줌.
 */

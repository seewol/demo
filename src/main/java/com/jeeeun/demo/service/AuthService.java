//package com.jeeeun.demo.service;
//
//import com.jeeeun.demo.common.error.BusinessException;
//import com.jeeeun.demo.common.error.ErrorCode;
//import com.jeeeun.demo.domain.member.Member;
//import com.jeeeun.demo.repository.MemberRepository;
//import com.jeeeun.demo.service.member.model.UserLoginCommand;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@RequiredArgsConstructor
//@Service
//public class AuthService {
//
//    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Transactional
//    public void login(UserLoginCommand command) {
//
//        // todo : 유저 email 있는지 확인 → 유저 있음 / 없으면 에러 반환
//        Member member = memberRepository.existsByEmailIsDeletedFalse(command.email())
//                .orElseThrow(() -> new BusinessException(ErrorCode.CONFLICT_USER));
//
//
//        // todo : 비밀번호 일치하는지 체크 / 일치 안 하면 에러 반환
//        if (!passwordEncoder.matches(command.password(), member.getPassword())) { // 입력받은 비밀번호, 암호화된 DB 내 비밀번호
//            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
//        }
//
//        // todo 토큰 만들기 (jwt)
//        jwtTokenProvider.createToken()
//
//        // todo 응답 (유저, 토큰)
//
//    }
//}

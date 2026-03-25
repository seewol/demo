package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.user.Provider;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.domain.user.UserCredentials;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    // 이메일 + 로그인 방식으로 인증 정보 조회
    // ex) 로컬 로그인 : provider = LOCAL, identifier = 이메일
    Optional<UserCredentials> findByIdentifierAndProvider(String identifier, Provider provider);

    void deleteByUser(User user);

}

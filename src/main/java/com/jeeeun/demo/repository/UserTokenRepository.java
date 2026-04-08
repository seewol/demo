package com.jeeeun.demo.repository;

import com.jeeeun.demo.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByRefreshToken(String refreshToken);

    void deleteByUserId(Long userId);

}

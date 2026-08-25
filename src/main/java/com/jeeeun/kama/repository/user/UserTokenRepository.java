package com.jeeeun.kama.repository.user;

import com.jeeeun.kama.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByRefreshToken(String refreshToken);

    void deleteByUserId(Long userId);

}

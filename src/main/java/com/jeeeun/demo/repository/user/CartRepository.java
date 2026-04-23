package com.jeeeun.demo.repository.user;

import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
                                        // Cart 엔티티 다루고, PK 타입은 Long
    Optional<Cart> findByUser(User user);
    // User → Cart 찾기 (어느 한 유저의 장바구니 가져올 때)
    // 회원가입 시 Cart 생성 → Item 담기 요청마다 해당 메서드 조회

}

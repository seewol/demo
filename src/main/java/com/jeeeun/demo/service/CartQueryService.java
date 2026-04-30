package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.repository.user.CartRepository;
import com.jeeeun.demo.repository.user.UserRepository;
import com.jeeeun.demo.service.cart.model.CartResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartQueryService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;


    // 내 장바구니 조회
    @Transactional(readOnly = true)
    public CartResult getCart(Long userId) {

        // 1. todo : 유저 조회
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // 2. todo : 장바구니 조회
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CART));

        return CartResult.from(cart);
    }
}

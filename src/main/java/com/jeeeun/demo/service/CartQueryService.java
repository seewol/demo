package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.repository.product.ProductStockRepository;
import com.jeeeun.demo.repository.user.CartRepository;
import com.jeeeun.demo.repository.user.UserRepository;
import com.jeeeun.demo.service.cart.model.CartItemResult;
import com.jeeeun.demo.service.cart.model.CartResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartQueryService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductStockRepository productStockRepository;


    // 내 장바구니 조회
    @Transactional(readOnly = true)
    public CartResult getCart(Long userId) {

        // 1. todo : 유저 조회
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // 2. todo : 장바구니 조회
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CART));

        // 3. CartItem마다 재고 조회 → isSoldOut 판단
        List<CartItemResult> items = cart.getCartItems().stream()
                .map(cartItem -> {
                    // 재고 row 없거나, 수량 0이면 품절
                    boolean isSoldOut = productStockRepository
                            .findByProductVariant_Id(cartItem.getProductVariant().getId())
                            .map(productStock -> productStock.getQuantity() == 0)
                            .orElse(true);

                    return CartItemResult.from(cartItem, isSoldOut);
                })
                .toList();

        // 스트림의 .map() → 리스트를 변환
        // Optional의 .map() → 값이 있을 때만 변환

        return CartResult.from(cart, items);
    }

}

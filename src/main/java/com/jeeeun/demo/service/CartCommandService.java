package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.product.ProductVariant;
import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.CartItem;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.repository.product.ProductVariantRepository;
import com.jeeeun.demo.repository.user.CartItemRepository;
import com.jeeeun.demo.repository.user.CartRepository;
import com.jeeeun.demo.repository.user.UserRepository;
import com.jeeeun.demo.repository.user.UserRepositoryCustom;
import com.jeeeun.demo.service.cart.model.CartItemCreateCommand;
import com.jeeeun.demo.service.cart.model.CartItemCreateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartCommandService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public CartItemCreateResult addCartItem(CartItemCreateCommand command) {

        // todo 1 : 유저 조회
        User user = userRepository.findByIdAndIsDeletedFalse(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // todo 2 : 해당 유저 장바구니 조회 -> 없으면 새로 생성
        // 유저마다 장바구니는 1개, 없으면 바로 만들어주기
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.from(user)));

        // todo 3 : variant 조회 (추가하려는 상품 조합의 실재 여부 확인)
        ProductVariant variant = productVariantRepository.findById(command.variantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_VARIANT));

        // todo 4 : 이미 장바구니에 담긴 아이템인지 확인 -> 있으면 수량만 +, 없으면 새 CartItem 생성
        CartItem cartItem = cartItemRepository.findByCartAndProductVariant(cart, variant)
                .orElseGet(() -> CartItem.from(cart, variant, 0L)); // long quantity

        // todo 5 : 수량 업데이트 (기존 수량 + 새로 추가하는 수량)
        cartItem.updateQuantity(cartItem.getQuantity() + command.quantity());

        cartItemRepository.save(cartItem);

        return CartItemCreateResult.from(cartItem);
    }

}

package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.product.ProductStock;
import com.jeeeun.demo.domain.product.ProductVariant;
import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.CartItem;
import com.jeeeun.demo.domain.user.User;
import com.jeeeun.demo.repository.product.ProductStockRepository;
import com.jeeeun.demo.repository.product.ProductVariantRepository;
import com.jeeeun.demo.repository.user.CartItemRepository;
import com.jeeeun.demo.repository.user.CartRepository;
import com.jeeeun.demo.repository.user.UserRepository;
import com.jeeeun.demo.service.cart.model.CartItemCreateCommand;
import com.jeeeun.demo.service.cart.model.CartItemCreateResult;
import com.jeeeun.demo.service.cart.model.CartItemUpdateCommand;
import com.jeeeun.demo.service.cart.model.CartItemUpdateResult;
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
    private final ProductStockRepository productStockRepository;


    // 장바구니 아이템 추가
    @Transactional
    public CartItemCreateResult addCartItem(CartItemCreateCommand command) {

        // ★ 1 : 유저 조회
        User user = userRepository.findByIdAndIsDeletedFalse(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // ★ 2 : 해당 유저 장바구니 조회 -> 없으면 새로 생성
        // 유저마다 장바구니는 1개, 없으면 바로 만들어주기
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.from(user)));

        // ★ 3 : variant 조회 (추가하려는 상품 조합의 실재 여부 확인)
        ProductVariant variant = productVariantRepository.findById(command.variantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_VARIANT));

        // ★ 3-1 : 재고 조회 (재고 row 없는 상품 / 재고 등록 안 된 상태 → 예외 처리)
        ProductStock stock = productStockRepository.findByProductVariant_Id(command.variantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

        // ★ 4 : 이미 장바구니에 담긴 아이템인지 확인 -> 있으면 수량만 +, 없으면 새 CartItem 생성
        CartItem cartItem = cartItemRepository.findByCartAndProductVariant(cart, variant)
                .orElseGet(() -> CartItem.from(cart, variant, 0L)); // long quantity

        // ★ 4-1 : 카트에 담긴 기존 수량 + 신규 추가 수량이 재고를 초과 → 예외 처리
        long totalQuantity = cartItem.getQuantity() + command.quantity();
        // note : 신규 추가 수량보다 재고가 적어도 함께 걸러짐 → (0 + 신규 추가 수량) > 재고
        if (totalQuantity > stock.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        // ★ 5 : 수량 업데이트 (기존 수량 + 새로 추가하는 수량)
        cartItem.updateQuantity(cartItem.getQuantity() + command.quantity());

        cartItemRepository.save(cartItem);

        return CartItemCreateResult.from(cartItem);
    }


    // 장바구니 아이템 수량 변경
    @Transactional
    public CartItemUpdateResult updateCartItemQuantity(CartItemUpdateCommand command) {

        // ★ 1 : cartItem 조회
        CartItem cartItem = cartItemRepository.findById(command.cartItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CART_ITEM));

        // ★ 2 : 내 카트에 추가된 아이템인지 검증 (타인의 장바구니 아이템 수정을 방지하기 위함!)
        if (!cartItem.getCart().getUser().getId().equals(command.userId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // ★ 3 : 해당 아이템 재고 조회
        ProductStock stock = productStockRepository.findByProductVariant_Id(
                        cartItem.getProductVariant().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

        // ★ 4 : 변경하려는 수량이 재고를 초과하는지 확인
        if (command.quantity() > stock.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        // ★ 5 : 수량 변경
        cartItem.updateQuantity(command.quantity());

        return CartItemUpdateResult.from(cartItem);
    }

    // 장바구니 아이템 삭제
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {

        // ★ 1 : cartItem 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CART_ITEM));

        // ★ 2 : 내 카트에 추가된 아이템인지 검증
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // ★ 3 : 삭제 처리
        cartItemRepository.delete(cartItem);

    }

}

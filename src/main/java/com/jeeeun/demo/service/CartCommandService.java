package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.product.Product;
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
import com.jeeeun.demo.service.cart.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        // ★ 4-1 : 1인당 최대 구매 수량 검증 + 수량 추가
        // Product에 maxPurchaseQuantity 설정되어 있으면, 그 값까지만 허용
        // null이면, 제한 없음 (addQuantity 내부에서 검증 스킵)
        Product product = variant.getProduct();

        cartItem.addQuantity(command.quantity(), product.getMaxPurchaseQuantity());

        // ★ 4-2 : 카트에 담긴 총 수량의 재고 초과 여부 확인
        // 구매 제한 검증, 재고 검증은 별개!
        // ex) maxPurchaseQuantity = 5, 재고 = 3 → 재고 부족으로 막혀야 함.
        if (cartItem.getQuantity() > stock.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

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

        // ★ 4 : 1인당 최대 구매 수량 검증 + 수량 변경
        Product product = cartItem.getProductVariant().getProduct();

        cartItem.updateQuantity(command.quantity(), product.getMaxPurchaseQuantity());

        // ★ 4-1 : 변경하려는 수량의 재고 초과 여부 확인
        if (command.quantity() > stock.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

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


    // 비로그인 장바구니 → 로그인 후 merge
    @Transactional
    public CartMergeResult mergeCart(CartMergeCommand command) {

        // ★ 1 : 유저 조회
        User user = userRepository.findByIdAndIsDeletedFalse(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        // ★ 2 : 장바구니 조회 → 없으면 새로 생성
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.from(user)));

        int mergedCount = 0;

        for (CartMergeCommand.Item item : command.items()) {

            // ★ 3 : variant 조회 → 없으면 스킵 (비로그인 중 삭제된 상품 대비)
            Optional<ProductVariant> variantOpt = productVariantRepository.findById(item.variantId());
            if (variantOpt.isEmpty()) {
                continue; // 루프 벗어나기
            }
            ProductVariant variant = variantOpt.get();

            // ★ 4 : 재고 → 품절이면 스킵
            Optional<ProductStock> stockOpt = productStockRepository.findByProductVariant_Id(item.variantId());
            if (stockOpt.isEmpty() || stockOpt.get().getQuantity() == 0) {
                continue;
            }

            Product product = variant.getProduct();

            // ★ 5 : 기존 장바구니 내 같은 variant 있는지 확인
            Optional<CartItem> existingOpt = cartItemRepository.findByCartAndProductVariant(cart, variant);

            if (existingOpt.isPresent()) {
                // ★ 5-1 : 기존 재고 있으면 수량 합산 → 한도 / 재고 초과시 스킵
                try {
                    CartItem existing = existingOpt.get();
                    long newQuantity = existing.getQuantity() + item.quantity();
                    if (newQuantity > stockOpt.get().getQuantity()) {
                        continue;
                    }
                    existing.addQuantity(item.quantity(), product.getMaxPurchaseQuantity());
                } catch (BusinessException e) {
                    continue;
                }

            } else {
                // ★ 5-2 : 기존 재고 없으면 새 아이템 추가 → 재고 초과 시 스킵
                if (item.quantity() > stockOpt.get().getQuantity()) {
                    continue;
                }

                // ★ 구매 한도 초과할 경우 스킵
                Integer maxPurchaseQuantity = product.getMaxPurchaseQuantity();
                if (maxPurchaseQuantity != null && item.quantity() > maxPurchaseQuantity) {
                    continue;
                }

                CartItem newItem = CartItem.from(cart, variant, item.quantity());
                cartItemRepository.save(newItem);
            }

            mergedCount++;
        }

        return CartMergeResult.from(mergedCount);
    }

}

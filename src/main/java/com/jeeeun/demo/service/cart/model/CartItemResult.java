package com.jeeeun.demo.service.cart.model;

import com.jeeeun.demo.domain.product.Product;
import com.jeeeun.demo.domain.user.CartItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Builder
public record CartItemResult(
        Long cartItemId,
        Long variantId,
        String productName,
        String variantName,
        BigDecimal salePrice,
        BigDecimal discountedPrice,
        long quantity
) {
    public static CartItemResult from(CartItem cartItem) {

        Product product = cartItem.getProductVariant().getProduct();
        BigDecimal additionalPrice = cartItem.getProductVariant().getAdditionalPrice();

        // ★ todo:  isDiscounted → 스케줄러가 관리하도록 한다!

        BigDecimal discountedPrice;

        if (product.isDiscounted() && product.getDiscountRate() != null) {

            // 할인가 (salePrice만 할인 적용 / 추가금은 예외)
            BigDecimal multiplier = BigDecimal.ONE
                    .subtract(BigDecimal.valueOf(product.getDiscountRate())
                            .divide(BigDecimal.valueOf(100)));

            discountedPrice = product.getSalePrice()
                    .multiply(multiplier)
                    .setScale(0, RoundingMode.HALF_UP)
                    .add(additionalPrice != null ? additionalPrice : BigDecimal.ZERO);
                            // └ 소수점 0자리 (정수 만들기) + 반올림
        } else {
            // 정가 (할인 X) + 추가금
            discountedPrice = product.getSalePrice()
                    .add(additionalPrice != null ? additionalPrice : BigDecimal.ZERO);
        }

        return CartItemResult.builder()
                .cartItemId(cartItem.getId())
                .variantId(cartItem.getProductVariant().getId())
                .productName(product.getName())
                .variantName(cartItem.getProductVariant().getVariantName())
                .salePrice(product.getSalePrice())
                .discountedPrice(discountedPrice)
                .quantity(cartItem.getQuantity())
                .build();
    }
}

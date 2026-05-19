package com.jeeeun.demo.domain.user;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.product.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cart_item")
public class CartItem extends BaseTimeEntity {

    // User (1) ── (1) Cart (1) ── (N) CartItem (N) ── (1) ProductVariant

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    public static CartItem from(Cart cart, ProductVariant productVariant, long quantity) {
        CartItem item = new CartItem();
        item.cart = cart;
        item.productVariant = productVariant;
        item.quantity = quantity;
        return item;
    }


    // ★ 수량 추가; 누적 추가 (장바구니에 같은 상품 또 담을 때)
    // 서버에서 기존 수량 +? 계산
    // 기존 수량 + 추가 수량이 maxPurchaseQuantity를 넘으면 예외
    public void addQuantity(long additionalQuantity, Integer maxPurchaseQuantity) {
        long totalQuantity = this.quantity + additionalQuantity;

        // note : maxPurchaseQuantity ≠ null 경우에만 검증
        // null이면 제한 없어서, 재고 검증만으로 충분!
        if (maxPurchaseQuantity != null && totalQuantity > maxPurchaseQuantity) {
            throw new BusinessException(ErrorCode.EXCEEDED_PURCHASE_LIMIT);
        }

        this.quantity = totalQuantity;
    }


    // ★ 수량 직접 변경; 바꿔치기 (프론트에서 최종 수량 넘겨줄 때)
    // 사용자가 +/- 버튼 눌러 계산한 결과 값 보내주면 서버에서 그 값 그대로 세팅
    public void updateQuantity(long quantity, Integer maxPurchaseQuantity) {
        if (maxPurchaseQuantity != null && quantity > maxPurchaseQuantity) {
            throw new BusinessException(ErrorCode.EXCEEDED_PURCHASE_LIMIT);
        }

        this.quantity = quantity;
    }


}

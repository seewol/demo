package com.jeeeun.demo.domain.user;

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

    public void updateQuantity(long quantity) {
        this.quantity = quantity;
    }

}

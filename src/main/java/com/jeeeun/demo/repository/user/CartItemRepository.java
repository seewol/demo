package com.jeeeun.demo.repository.user;

import com.jeeeun.demo.domain.product.ProductVariant;
import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant variant);

}

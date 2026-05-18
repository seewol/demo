package com.jeeeun.demo.repository.user;

import com.jeeeun.demo.domain.product.ProductVariant;
import com.jeeeun.demo.domain.user.Cart;
import com.jeeeun.demo.domain.user.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant variant);

    // updatedAt 기준 오래된 CartItem 일괄 삭제
    // ex) 30일 동안 아무 변경 없었던 아이템은 삭제
    @Modifying(clearAutomatically = true)
    @Query("""
            delete from CartItem ci
            where ci.updatedAt < :threshold
            """)    // Bulk delete
    int deleteExpiredCartItems(@Param("threshold")LocalDateTime threshold);

}

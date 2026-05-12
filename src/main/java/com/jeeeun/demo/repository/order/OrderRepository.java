package com.jeeeun.demo.repository.order;

import com.jeeeun.demo.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    // 취소 시 orderItems 한 번에 모두 조회 (N+1 방지)
    @Query("""
        select o from Order o
        join fetch o.orderItems oi
        join fetch oi.productVariant
        where o.id = :orderId
    """)
    Optional<Order> findWithItemsById(Long orderId);
    // :orderId → 파라미터 바인딩
    // join fetch → 조인과 동시에 데이터 가져오란 뜻
    // LAZY 로딩 → 실제로 쓸 당시에 DB 조회

}

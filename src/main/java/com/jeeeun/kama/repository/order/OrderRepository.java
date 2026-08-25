package com.jeeeun.kama.repository.order;

import com.jeeeun.kama.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Webhook에서 imp_uid로 어떤 주문인지 찾을 때 사용
    // (Webhook에는 orderId가 없고, 내가 결제 검증 후 Order에 저장해둔 imp_uid만 옴)
    Optional<Order> findByImpUid(String impUid);

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

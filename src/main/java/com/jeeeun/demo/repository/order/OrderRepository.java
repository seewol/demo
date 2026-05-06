package com.jeeeun.demo.repository.order;

import com.jeeeun.demo.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

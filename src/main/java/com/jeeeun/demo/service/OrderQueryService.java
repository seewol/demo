package com.jeeeun.demo.service;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.repository.order.OrderRepository;
import com.jeeeun.demo.service.order.model.OrderDetailResult;
import com.jeeeun.demo.service.order.model.OrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public Page<OrderResult> getOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResult::from);
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public OrderDetailResult getOrder(Long userId, Long orderId) {

        // ★ 1 : 주문 조회 (없을시 예외)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER));

        // ★ 2 : 본인 주문인지 검증 (userId 불일치시 예외)
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // ★ 3 : OrderDetailResult 반환
        return OrderDetailResult.from(order);
    }

}

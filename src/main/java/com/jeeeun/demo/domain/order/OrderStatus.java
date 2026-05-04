package com.jeeeun.demo.domain.order;

public enum OrderStatus {
    PENDING,    // 주문 완료 (결제 대기 상태)
    PAID,       // 결제 완료
    PREPARING,  // 상품 준비중
    SHIPPING,   // 배송중
    DELIVERED,  // 배송 완료
    CANCELLED   // 취소됨
}

package com.jeeeun.demo.domain.order;

public enum OrderStatus {
    PENDING,    // 결제 대기 (무통장 입금 등 추후 사용 예정)
    PAID,       // 결제 완료 (포트원 결제 검증 통과 후 생성)
    PREPARING,  // 상품 준비중
    SHIPPING,   // 배송중
    DELIVERED,  // 배송 완료
    CANCELLED   // 취소됨
}

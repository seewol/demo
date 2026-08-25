package com.jeeeun.kama.domain.shipping;

public enum ShippingStatus {
    PREPARING,  // 상품준비중 (Shipping 생성 시 기본 상태)
    DELAYED,    // 발송지연 (PREPARING 상태에서만 진입 가능)
    SHIPPING,   // 배송중
    DELIVERED,  // 배송완료
    CANCELLED   // 주문 취소시 함께 전환 (PREPARING/DELAYED 상태에서만 가능)
}

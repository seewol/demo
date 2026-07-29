package com.jeeeun.demo.domain.order;

public enum OrderStatus {
    PENDING,    // 결제 대기 (무통장 입금 등 추후 사용 예정)
    PAID,       // 결제 완료 (포트원 결제 검증 통과 후 생성)
    CANCELLED   // 취소됨

    // NOTE : PREPARING/SHIPPING/DELEVERED → ShippingStatus로 이동

    // NOTE : 주문상태(결제)와 배송상태를 분리한 이유
    // 결제는 했어도 '아직 준비중, 배송중, 배송완료' 이 세 개는
    // Order 입장에서 전부 PAID 상태
    // → 실제 값이 바뀌는 주체는 Shipping이니 스스로 관리하도록 함.

}

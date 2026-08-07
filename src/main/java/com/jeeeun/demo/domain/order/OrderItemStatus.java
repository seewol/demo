package com.jeeeun.demo.domain.order;

// 주문한 아이템의 상태
public enum OrderItemStatus {

    ORDERED,             // 정상 주문 상태 = 기본값
    PARTIALLY_CANCELLED, // 일부 수량만 취소된 상태
    CANCELLED            // 취소된 아이템 (부분 취소 시 개별 아이템마다 표시됨)

}

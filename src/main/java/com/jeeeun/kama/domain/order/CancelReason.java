package com.jeeeun.kama.domain.order;

public enum CancelReason {

    CHANGE_OF_MIND("단순 변심"),
    WRONG_ORDER("주문 실수"),
    DELIVERY_DELAY("배송 지연"),
    SELLER_FAULT("상품 하자/오배송"),
    OTHER("기타");

    private final String description;

    CancelReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
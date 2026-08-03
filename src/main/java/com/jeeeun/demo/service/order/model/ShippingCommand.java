package com.jeeeun.demo.service.order.model;

import lombok.Builder;

@Builder
public record ShippingCommand(

        String receiverName,    // 수령인 이름
        String receiverPhone,   // 수령인 연락처 (정규화 해서 들어옴 ex : 01012345678)
        String zipCode,         // 우편번호
        String address,         // 기본 주소
        String addressDetail,   // 상세 주소
        String deliveryRequest  // 배송 요청사항 (선택 값)

) {
}

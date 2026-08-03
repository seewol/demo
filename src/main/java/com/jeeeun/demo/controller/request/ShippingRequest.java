package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.order.model.ShippingCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ShippingRequest(

        @NotBlank(message = "수령인 이름은 필수입니다.")
        String receiverName,

        @NotBlank(message = "수령인 연락처는 필수입니다.")
        String receiverPhone,

        @NotBlank(message = "우편번호는 필수입니다.")
        String zipCode,

        @NotBlank(message = "주소는 필수입니다.")
        String address,

        @NotBlank(message = "상세주소는 필수입니다.")
        String addressDetail,

        String deliveryRequest
) {

    public ShippingCommand toCommand() {

        String normalizedPhone = receiverPhone.replaceAll("[^0-9]", "");

        if (!normalizedPhone.matches("010\\d{7,8}$")) {
            throw new IllegalArgumentException("수령인 연락처 형식이 올바르지 않습니다.");
        }

        return ShippingCommand.builder()
                .receiverName(receiverName)
                .receiverPhone(normalizedPhone) // 정규화된 연락처
                .zipCode(zipCode)
                .address(address)
                .addressDetail(addressDetail)
                .deliveryRequest(deliveryRequest)
                .build();
    }
}

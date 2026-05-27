package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.order.model.DirectOrderCreateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DirectOrderCreateRequest(

        // 구매할 조합
        @NotNull(message = "상품 조합을 선택해주세요.")
        Long variantId,

        // 구매할 수량
        @NotNull
        @Min(value = 1, message = "최소 구매 수량은 1개입니다.")
        long quantity,

        // 포트원 결제 고유번호
        @NotBlank(message = "결제 정보가 누락되었습니다.")
        String impUid
) {

    public DirectOrderCreateCommand toCommand(Long userId) {
        return DirectOrderCreateCommand.builder()
                .userId(userId)
                .variantId(variantId)
                .quantity(quantity)
                .impUid(impUid)
                .build();
    }
}

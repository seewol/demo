package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.shipping.model.ShippingDelayCommand;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ShippingDelayRequest(

        // 맨날 잊어먹는 @NotNull과 @NotBlank의 차이 (후자가 더 엄격한 ver)
        // @NotNull : 모든 타입 ("" 빈 문자열, "  " 공백은 못 막음)
        // @NotBlank : null 뿐만 아니라 빈 문자열, 공백 다 막음 (String 전용 ─ 다른 타입은 컴파일 에러)

        @NotBlank(message = "지연 사유 입력은 필수입니다.")
        String reason,

        @NotNull(message = "새로운 발송 예정일 입력은 필수입니다.")
        @FutureOrPresent(message = "발송 예정일은 오늘 이후여야 합니다.")
        LocalDate newExpectedShipDate

        // @FutureOrPresent : 오늘 이전 과거 날짜가 들어오면 요청에 400 응답

) {
    // shippingId는 body에 없고, URL(@PathVariable)로 오니, 파라미터로 받아 채우기
    public ShippingDelayCommand toCommand(Long shippingId) {
        return ShippingDelayCommand.builder()
                .shippingId(shippingId)
                .reason(reason)
                .newExpectedShipDate(newExpectedShipDate)
                .build();
    }
}



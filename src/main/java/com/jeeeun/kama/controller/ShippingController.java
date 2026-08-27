package com.jeeeun.kama.controller;

import com.jeeeun.kama.controller.request.ShippingDelayRequest;
import com.jeeeun.kama.controller.response.ShippingStatusResponse;
import com.jeeeun.kama.service.ShippingCommandService;
import com.jeeeun.kama.service.shipping.model.ShippingStatusResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shippings")
@Tag(name = "배송", description = "관리자용 배송 상태 변경 API")
public class ShippingController {

    private final ShippingCommandService shippingCommandService;

    // ★ 배송 시작 처리
    // PATCH /shippings/{shippingId}/ship
    @Operation(summary = "배송 시작 처리")
    @ApiResponse(responseCode = "200", description = "배송 시작 처리 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 배송 정보입니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{shippingId}/ship")
    public ShippingStatusResponse ship(
            @PathVariable Long shippingId
    ) {
        ShippingStatusResult result = shippingCommandService.ship(shippingId);

        return ShippingStatusResponse.from(result);
    }

    // ★ 발송 지연 처리
    // PATCH /shippings/{shippingId}/delay
    @Operation(summary = "발송 지연 처리", description = "지연 사유 & 발송 예정일을 새로 입력받아 배송 상태를 DELAYED로 변경합니다. ")
    @ApiResponse(responseCode = "200", description = "발송 지연 처리 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 배송 정보입니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{shippingId}/delay")
    public ShippingStatusResponse delay(
            @PathVariable Long shippingId,
            @Valid @RequestBody ShippingDelayRequest request
    ) {
        ShippingStatusResult result = shippingCommandService.delay(request.toCommand(shippingId));

        return ShippingStatusResponse.from(result);
    }

    // ★ 배송 완료 처리
    // PATCH /shippings/{shippingId}/deliver
    @Operation(summary = "배송 완료 처리")
    @ApiResponse(responseCode = "200", description = "배송 완료 처리 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 배송 정보입니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{shippingId}/deliver")
    public ShippingStatusResponse deliver(
            @PathVariable Long shippingId
    ) {
        ShippingStatusResult result = shippingCommandService.deliver(shippingId);

        return ShippingStatusResponse.from(result);
    }

}

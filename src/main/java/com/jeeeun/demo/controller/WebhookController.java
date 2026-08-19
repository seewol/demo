package com.jeeeun.demo.controller;

import com.jeeeun.demo.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// ★ 외부 결제사(포트원)가 우리 서버로 호출하는 Webhook 전용 컨트롤러
// 우리 프론트가 아니라 포트원 서버가 직접 호출하는 요청이라 JWT 토큰이 없음!
// → SecurityConfig에서 "/webhooks/**"를 permitAll() 처리해둠
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/webhooks")
@Tag(name = "WebhookController", description = "외부 결제사(포트원) Webhook 수신 엔드포인트")
public class WebhookController {

    private final WebhookService webhookService;

    // ★ 포트원(PortOne V1) 결제 상태 변경 Webhook 수신
    // 가상계좌 입금 완료 등, 결제 상태가 바뀔 때마다 포트원이 이 URL로 호출해준다.
    // (포트원 관리자 콘솔 → 결제 연동 → Webhook 설정에 이 URL 등록해야만 실제로 호출 가능)

    // NOTE : 포트원 V1 Webhook은 JSON 아니고, application/x-www-form-urlencoded 형식으로
    // imp_uid / merchant_uid / status 보내줌 → @RequestBody 아니고 @RequestParam으로 받아야 함.

    @Operation(summary = "포트원 결제 웹훅 수신", description = "가상계좌 입금 완료 등 결제 상태 변화가 있으면 포트원이 호출합니다.")
    @ApiResponse(responseCode = "200", description = "웹훅 정상 수신 (실제 반영 여부와 무관, 항상 200")
    @PostMapping("/port-one")
    @ResponseStatus(HttpStatus.OK)
    public void handlePortOneWebhook(
            @RequestParam("imp_uid") String impUid,
            @RequestParam("merchant_uid") String merchantUid,
            @RequestParam(value = "status", required = false) String status
    ) {
        log.info("포트원 Webhook 수신 ─ imp_uid : {}, merchant_uid : {}, status : {}", impUid, merchantUid, status);

        // status는 여기서 안 쓰고, WebhookService에서 imp_uid로 포트원에 재조회 후
        // 진짜! 상태를 직접 확인할 것 (웹훅 바디는 위조 가능해서 신뢰하지 않는 게 좋다.)
        webhookService.handlePortOnePayment(impUid);
    }

}

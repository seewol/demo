package com.jeeeun.demo.external.portone;

import com.jeeeun.demo.common.config.PortOneProperties;
import com.jeeeun.demo.service.order.model.PortOnePaymentResponse;
import com.jeeeun.demo.service.order.model.PortOneTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortOneClient {

    private final PortOneProperties portOneProperties;

    // (V1) 포트원 액세스 토큰 발급 URL
    private static final String TOKEN_URL = "https://api.iamport.kr/users/getToken";

    // (V1) 포트원 결제 정보 조회 URL - 뒤에 imp_uid 붙여 사용
    private static final String PAYMENT_URL = "https://api.iamport.kr/payments/";

    // imp_uid ? 포트원이 결제 건마다 발급해주는 결제 고유번호


    // ★ 1: API key + Secret → 액세스 토큰 발급
    // 포트원 API 호출 시마다 해당 토큰을 헤더에 담아야 한다.
    // private 이유? → PortOneClient 내부에서만 사용 (외부 노출 불필요)
    private String getAccessToken() {

        // 포트원이 요구하는 요청 바디
        Map<String, String> body = Map.of(
                "imp_key", portOneProperties.getApiKey(),
                "imp_secret", portOneProperties.getApiSecret()
        );

        // 포트원 서버에 POST 요청 → 액세스 토큰 받아오기
        PortOneTokenResponse response = RestClient.create()
                .post()
                .uri(TOKEN_URL)
                .body(body)
                .retrieve()     // 실제 요청 보내고 응답 받아오겠다는 선언
                .body(PortOneTokenResponse.class);  // 응답 내용 해당 객체로 자동 변환

        return response.response().accessToken();
    }


    // ★ 2: imp_uid → 결제 정보 조회
    // 프론트에서 imp_uid 보내면, 실제 결제 여부를 포트원 서버에서 확인
    public PortOnePaymentResponse.PortOnePaymentBody getPayment(String impUid) {

        log.info("포트원 결제 조회 URL: {}", PAYMENT_URL + impUid);

        // 위에 발급 받은 액세스 토큰
        String accessToken = getAccessToken();

        // 포트원 서버에 GET 요청 → 결제 정보 받아오기
        PortOnePaymentResponse response = RestClient.create()
                .get()
                .uri(PAYMENT_URL + impUid + "?include_sandbox=true")  // /payments/imp_0123456 형식
                .header("Authorization", accessToken)
                .retrieve()
                .body(PortOnePaymentResponse.class);

        return response.response();
    }


    // ★ 3: imp_uid로 환불 요청 (전액 환불) ─ 3-1과 오버로딩
    public void cancelPayment(String impUid, String reason) {
        cancelPayment(impUid, null, reason);
    }


    // ★ 3-1: imp_uid로 환불 요청 (부분 환불)
    // PAID 상태 주문 취소 시, 포트원에 환불 요청을 보내야만 실 결제가 취소된다.
    // amount → null이면 전액 취소, 값이 있으면 해당 금액만큼만 부분 취소!
    public void cancelPayment(String impUid, BigDecimal amount, String reason) {

        String accessToken = getAccessToken();

        // amount는 있을 수도, 없을 수도 있다. → 불변인 Map.of() 대신 가변 Map 사용
        Map<String, Object> body = new HashMap<>();
        body.put("imp_uid", impUid);
        body.put("reason", reason);   // ★ 하드코딩 제거, 파라미터로 대체
        if (amount != null) {   // 조건부로 값을 넣어야 해서 가변 HashMap을 쓴 거다.
            body.put("amount", amount);
        }

        RestClient.create()
                .post()
                .uri("https://api.iamport.kr/payments/cancel")
                .header("Authorization", accessToken)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

}
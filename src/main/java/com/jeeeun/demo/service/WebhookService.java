package com.jeeeun.demo.service;

import com.jeeeun.demo.domain.order.Order;
import com.jeeeun.demo.external.portone.PortOneClient;
import com.jeeeun.demo.repository.order.OrderRepository;
import com.jeeeun.demo.service.order.model.PortOnePaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookService {

    private final OrderRepository orderRepository;
    private final PortOneClient portOneClient;

    // ★ 포트원 웹훅 처리
    // 가상계좌에 돈이 실제로 입금되면, 포트원이 해당 메서드를 호출하게 됨 (WebhookController 경유)

    // NOTE : 웹훅 요청에 담겨오는 status 값은 절대 그대로 믿지 말자.
    // 웹훅은 '외부에서 우리 서버로 오는 요청'이므로 누구든 imp_uid 알면 해킹 가능
    // → 고로 imp_uid로 포트원 결제조회 API를 재호출해, 진짜 결제 상태를 서버가 직접 확인할 것!
    // (현재 카드 결제 검증 로직에서 사용중인 것과 똑같은 패턴)

    @Transactional
    public void handlePortOnePayment(String impUid) {

        // ★ 1 : imp_uid로 우리 주문 조회
        Order order = orderRepository.findByImpUid(impUid).orElse(null);

        if (order == null) {
            log.warn("Webhook 처리 실패 ─ 해당 imp_uid 주문을 찾을 수 없음 : {}", impUid);
            // 로그만 찍는 이유 : 여기서 예외 던지면 포트원이 "실패"로 판단해서 같은 웹훅을 계속 재시도함.
            // 나한테 없는 imp_uid는 문제가 아니므로, 로그만 남기고 정상 종료(200 응답)해 재시도 막음.
            return;
        }

        // ★ 2 : 포트원 서버에 실제 결제 상태 재조회 (Webhook body 신뢰 안 함)
        PortOnePaymentResponse.PortOnePaymentBody paymentBody = portOneClient.getPayment(impUid);

        // ★ 3 : 실제 "paid"가 확인될 때만 PENDING → PAID로 전환
        if ("paid".equals(paymentBody.status())) {
            order.completePayment();
            log.info("가상계좌 입금 확인 완료 ─ orderId : {}, impUid : {}", order.getId(), impUid);
        } else {
            log.info("Webhook 수신했으나, 아직 결제완료 상태 아님 ─ impUid : {}, status : {}",
                    impUid, paymentBody.status());
        }
    }
}

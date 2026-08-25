package com.jeeeun.kama.service;

import com.jeeeun.kama.external.portone.PortOneClient;
import com.jeeeun.kama.repository.order.OrderRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.jeeeun.kama.domain.order.Order;
import com.jeeeun.kama.domain.order.OrderStatus;
import com.jeeeun.kama.domain.user.User;
import com.jeeeun.kama.service.order.model.PortOnePaymentResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PortOneClient portOneClient;

    @InjectMocks
    private WebhookService webhookService;


    @Test
    void 존재하지_않는_imp_uid는_예외없이_조용히_종료() {

        // ── given ──────────────────────────────────────────

        // 이 imp_uid로는 우리 DB에 주문이 없다고 가정
        given(orderRepository.findByImpUid("unknown_imp")).willReturn(Optional.empty());

        // ── when & then ────────────────────────────────────

        // 예외가 하나도 안 터지는지만 확인하면 되는 테스트
        assertThatCode(() -> webhookService.handlePortOnePayment("unknown_imp"))
                .doesNotThrowAnyException();
        // assertThat(값).isEqualTo(...)는 결과값 검증이었다면
        // .doesNotThrowAnyException()은 해당 코드 실행 시 예외 발생 여부를 검증하는 문법

        // 주문이 아예 없으니, 포트원한테 굳이 재조회할 필요도 없어야 한다
        verify(portOneClient, never()).getPayment(any());
    }


    @Test
    void 정상_웹훅_수신시_PENDING_주문이_PAID로_바뀜() {

        // ── given ──────────────────────────────────────────

        User user = User.from("지은", "jeeeunpark@gmail.com", "01012345678");

        // 가상계좌 발급 상태(PENDING)의 주문을 하나 만들어둔다
        Order order = Order.fromVirtualAccount(
                user, BigDecimal.valueOf(10_000), "imp_789",
                "국민은행", "1234567890", "홍길동", LocalDateTime.now()
        );

        given(orderRepository.findByImpUid("imp_789")).willReturn(Optional.of(order));

        // 포트원에 재조회하면 "paid"로 확인된다고 가정
        PortOnePaymentResponse.PortOnePaymentBody payment = new PortOnePaymentResponse.PortOnePaymentBody(
                "imp_789", "merchant_3", BigDecimal.valueOf(10_000), "paid",
                null, null, null, null
        );
        given(portOneClient.getPayment("imp_789")).willReturn(payment);

        // ── when ───────────────────────────────────────────

        webhookService.handlePortOnePayment("imp_789");

        // ── then ───────────────────────────────────────────

        // order는 mock이 아니라 우리가 직접 만든 진짜 객체라서,
        // completePayment()가 실행되면 이 객체의 상태가 실제로 바뀐다.
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void 이미_PAID인_주문에_웹훅이_중복으로_와도_안전() {

        // ── given ──────────────────────────────────────────

        User user = User.from("지은", "jeeeunpark@gmail.com", "01012345678");

        Order order = Order.fromVirtualAccount(
                user, BigDecimal.valueOf(10_000), "imp_999",
                "국민은행", "1234567890", "홍길동", LocalDateTime.now()
        );
        order.completePayment(); // 이미 한 번 PAID로 전환해둔 상태 (포트원이 웹훅을 재전송한 상황 흉내)

        given(orderRepository.findByImpUid("imp_999")).willReturn(Optional.of(order));

        PortOnePaymentResponse.PortOnePaymentBody payment = new PortOnePaymentResponse.PortOnePaymentBody(
                "imp_999", "merchant_4", BigDecimal.valueOf(10_000), "paid",
                null, null, null, null
        );
        given(portOneClient.getPayment("imp_999")).willReturn(payment);

        // ── when ───────────────────────────────────────────

        // 같은 웹훅이 한 번 더 온 상황을 흉내낸다
        webhookService.handlePortOnePayment("imp_999");

        // ── then ───────────────────────────────────────────

        // 예외 없이 조용히 끝나고, 상태는 여전히 PAID여야 한다 (PENDING → PAID로 "또" 바뀌면 안 됨)
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

}
package com.jeeeun.kama.service;

import com.jeeeun.kama.domain.order.CancelReason;
import com.jeeeun.kama.domain.order.Order;
import com.jeeeun.kama.domain.order.OrderItem;
import com.jeeeun.kama.external.portone.PortOneClient;
import com.jeeeun.kama.repository.order.OrderRepository;
import com.jeeeun.kama.repository.product.ProductImageRepository;
import com.jeeeun.kama.repository.product.ProductStockRepository;
import com.jeeeun.kama.repository.product.ProductVariantRepository;
import com.jeeeun.kama.repository.user.CartItemRepository;
import com.jeeeun.kama.repository.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jeeeun.kama.domain.order.OrderStatus;
import com.jeeeun.kama.domain.product.Product;
import com.jeeeun.kama.domain.product.ProductImage;
import com.jeeeun.kama.domain.product.ProductStock;
import com.jeeeun.kama.domain.product.ProductVariant;
import com.jeeeun.kama.domain.user.CartItem;
import com.jeeeun.kama.domain.user.User;
import com.jeeeun.kama.service.order.model.OrderCreateCommand;
import com.jeeeun.kama.service.order.model.OrderCreateResult;
import com.jeeeun.kama.service.order.model.PortOnePaymentResponse;
import com.jeeeun.kama.service.order.model.ShippingCommand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// JUnit5 : 자바 개발자가 사용하는 테스팅 기반 프레임워크
@ExtendWith(MockitoExtension.class) // Mockito 기능 사용을 JUnit5에게 알리는 어노테이션
class OrderCommandServiceTest {

    @Mock   // 진짜 DB 접근 대신, 아무 동작 없는 가짜 객체를 만들어 필드에 넣어줌.
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private PortOneClient portOneClient;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @InjectMocks    // 진짜 테스트 하려는 대상
    private OrderCommandService orderCommandService;
    // 위에서 만든 7개 @Mock 객체를 보고, OrderCommandService의 생성자에 자동으로 꽂아줌!


    @Test
    void 카드결제_주문은_PAID로_생성() {

        // ── given (테스트용 가짜 데이터 준비) ──────────────────────

        // 상품 : 10,000원짜리, 할인 없음
        Product product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .salePrice(BigDecimal.valueOf(10_000))
                .isDiscounted(false)
                .build();

        // 상품 옵션(variant) : 추가금 없음
        ProductVariant variant = ProductVariant.from(product, null, null, null, "기본", null);

        // 재고 10개
        ProductStock stock = ProductStock.create(variant, 10);

        // 장바구니에 담긴 아이템 : 1개 주문
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .productVariant(variant)
                .quantity(1)
                .build();

        // 상품 썸네일
        ProductImage productImage = ProductImage.builder()
                .id(1L)
                .product(product)
                .imageUrl("https://example.com/thumbnail.jpg")
                .imageOrder(1)
                .build();

        // 주문할 유저
        User user = User.from("지은", "jeeeunpark@gmail.com", "01012345678");

        // 배송지 정보
        ShippingCommand shippingCommand = ShippingCommand.builder()
                .receiverName("지은")
                .receiverPhone("01012345678")
                .zipCode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .deliveryRequest("문 앞에 놔주세요")
                .build();

        // 컨트롤러에서 넘어왔다고 가정할 커맨드 객체
        // (실제로 없는 cartItemId를 쓰면 안 되니, 위 cartItem.getId()와 반드시 맞춰준다)
        OrderCreateCommand command = OrderCreateCommand.builder()
                .userId(1L)
                .cartItemIds(List.of(1L))
                .impUid("imp_123")
                .shipping(shippingCommand)
                .build();

        // 포트원에 "결제 검증"을 물어보면 이렇게 응답해준다고 가정
        // 상품가 10,000원 + 배송비 3,000원(5만원 미만이라 유료배송) = 13,000원이 결제된 것으로 맞춰야
        // 서비스 내부의 금액 검증(paidAmount == payAmount)을 통과한다.
        PortOnePaymentResponse.PortOnePaymentBody payment = new PortOnePaymentResponse.PortOnePaymentBody(
                "imp_123", "merchant_1", BigDecimal.valueOf(13_000), "paid",
                null, null, null, null
        );

        given(portOneClient.getPayment("imp_123")).willReturn(payment);
        given(userRepository.findByIdAndIsDeletedFalse(1L)).willReturn(Optional.of(user));
        given(cartItemRepository.findAllById(List.of(1L))).willReturn(List.of(cartItem));
        given(productStockRepository.findByProductVariant_Id(any())).willReturn(Optional.of(stock));
        given(productImageRepository.findThumbnailByProductId(any())).willReturn(Optional.of(productImage));

        // save()에 넘어온 Order 객체를 그대로 돌려주도록 흉내
        given(orderRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // ── when (실제로 테스트하려는 메서드 실행) ──────────────────

        OrderCreateResult result = orderCommandService.createOrder(command);

        // ── then (결과 검증) ───────────────────────────────────

        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
        assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
        assertThat(result.vbankNum()).isNull();     // 카드결제니까 가상계좌 정보는 전부 null이어야 함
        assertThat(result.vbankName()).isNull();
    }


    @Test
    void 가상계좌_주문은_PENDING으로_생성되고_계좌정보를_포함() {

        // ── given ──────────────────────────────────────────

        Product product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .salePrice(BigDecimal.valueOf(10_000))
                .isDiscounted(false)
                .build();

        ProductVariant variant = ProductVariant.from(product, null, null, null, "기본", null);
        ProductStock stock = ProductStock.create(variant, 10);

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .productVariant(variant)
                .quantity(1)
                .build();

        ProductImage productImage = ProductImage.builder()
                .id(1L)
                .product(product)
                .imageUrl("https://example.com/thumbnail.jpg")
                .imageOrder(1)
                .build();

        User user = User.from("지은", "jeeeunpark@gmail.com", "01012345678");

        ShippingCommand shippingCommand = ShippingCommand.builder()
                .receiverName("지은")
                .receiverPhone("01012345678")
                .zipCode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .deliveryRequest("문 앞에 놔주세요")
                .build();

        OrderCreateCommand command = OrderCreateCommand.builder()
                .userId(1L)
                .cartItemIds(List.of(1L))
                .impUid("imp_456")
                .shipping(shippingCommand)
                .build();

        // 가상계좌 발급 : status가 "paid"가 아니라 "ready", 계좌 정보(vbank_*)가 채워져서 온다.
        // vbankDate는 포트원이 초 단위 Unix Timestamp로 내려준다 (예: 2026-01-01 00:00:00 KST)
        long vbankDateEpochSeconds = 1767225600L;
        PortOnePaymentResponse.PortOnePaymentBody payment = new PortOnePaymentResponse.PortOnePaymentBody(
                "imp_456", "merchant_2", BigDecimal.valueOf(13_000), "ready",
                "1234567890", "국민은행", "홍길동", vbankDateEpochSeconds
        );

        given(portOneClient.getPayment("imp_456")).willReturn(payment);
        given(userRepository.findByIdAndIsDeletedFalse(1L)).willReturn(Optional.of(user));
        given(cartItemRepository.findAllById(List.of(1L))).willReturn(List.of(cartItem));
        given(productStockRepository.findByProductVariant_Id(any())).willReturn(Optional.of(stock));
        given(productImageRepository.findThumbnailByProductId(any())).willReturn(Optional.of(productImage));
        given(orderRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // ── when ───────────────────────────────────────────

        OrderCreateResult result = orderCommandService.createOrder(command);

        // ── then ───────────────────────────────────────────

        assertThat(result.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
        assertThat(result.vbankNum()).isEqualTo("1234567890");
        assertThat(result.vbankName()).isEqualTo("국민은행");
        assertThat(result.vbankHolder()).isEqualTo("홍길동");

        // vbankDueDate는 서비스 내부에서 Unix Timestamp → LocalDateTime으로 변환된 값이어야 한다.
        LocalDateTime expectedDueDate = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(vbankDateEpochSeconds), ZoneId.systemDefault());
        assertThat(result.vbankDueDate()).isEqualTo(expectedDueDate);
    }


    @Test
    void 입금기한_지난_PENDING_주문_자동_만료_처리() {

        // ── given ──────────────────────────────────────────

        User user = User.from("지은", "jeeeunpark@gmail.com", "01012345678");

        Product product = Product.builder()
                .id(1L)
                .name("텀블러")
                .salePrice(BigDecimal.valueOf(10_000))
                .isDiscounted(false)
                .build();

        ProductVariant variant = ProductVariant.from(product, null, null, null, "기본", null);
        ProductStock stock = ProductStock.create(variant, 5);   // 현재 재고 5개

        // 입금기한이 이미 지난 PENDING 주문
        Order order = Order.fromVirtualAccount(
                user, BigDecimal.valueOf(20_000), "imp_expired",
                "국민은행", "1234567890", "홍길동",
                LocalDateTime.now().minusDays(1)
        );

        OrderItem orderItem = OrderItem.from(
                order, variant, 2, "텀블러", "기본",
                BigDecimal.valueOf(10_000), BigDecimal.ZERO, BigDecimal.valueOf(10_000), "thumb.jpg"
        );
        order.getOrderItems().add(orderItem);

        given(orderRepository.findExpiredVirtualAccountOrders(eq(OrderStatus.PENDING), any()))
                .willReturn(List.of(order));
        given(productStockRepository.findByProductVariant_Id(any()))
                .willReturn(Optional.of(stock));

        // ── when ───────────────────────────────────────────

        int expiredCount = orderCommandService.expireVirtualAccountOrders();

        // ── then ───────────────────────────────────────────

        assertThat(expiredCount).isEqualTo(1);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancelReason()).isEqualTo(CancelReason.VBANK_EXPIRED);
        assertThat(stock.getQuantity()).isEqualTo(7);   // 5(기존) + 2(복구) = 7

        verify(portOneClient).cancelPayment("imp_expired", CancelReason.VBANK_EXPIRED.getDescription());
    }


}
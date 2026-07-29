package com.jeeeun.demo.domain.shipping;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"order"})  // 연관 관계 무한 루프 방지!
@Entity
@Table(name = "shipping")
public class Shipping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id", nullable = false)
    private Long id;

    // Order : Shipping → 1:1, FK는 Shipping이 가지고 있음 (연관관계 주인)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // 수령인 정보 (주문 시점에 스냅샷)
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    // 배송지 (주문 시점에 스냅샷)
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "delivery_request")
    private String deliveryRequest;

    // 배송비 및 배송상태
    @Column(name = "shipping_fee", nullable = false)
    private BigDecimal shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false)
    private ShippingStatus status;

    @Column(name = "expected_ship_date", nullable = false)
    private LocalDate expectedShipDate;     // 발송 예정일 (발송 기한)

    @Column(name = "delay_reason")
    private String delayReason;             //  지연 사유 (상태가 DELAYED 일 때만 값 있음)

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;


    public static Shipping from(
            Order order,
            String receiverName, String receiverPhone,
            String zipCode, String address, String addressDetail,
            String deliveryRequest, BigDecimal shippingFee, LocalDate expectedShipDate
    ) {
        Shipping shipping = new Shipping();
        shipping.order = order;
        shipping.receiverName = receiverName;
        shipping.receiverPhone = receiverPhone;
        shipping.zipCode = zipCode;
        shipping.address = address;
        shipping.addressDetail = addressDetail;
        shipping.deliveryRequest = deliveryRequest;
        shipping.shippingFee = shippingFee;
        shipping.status = ShippingStatus.PREPARING; // 생성 시점엔 늘 상품준비중
        shipping.expectedShipDate = expectedShipDate;

        order.assignShipping(shipping); // Order 쪽 연관관계도 함께 연결
        // 주인 아닌 쪽(Order)도 수동으로 채워 연결해줘야 함

        return shipping;
    }


    // ★ 발송지연 처리 ─ PREPARING 상태에서만 가능
    // 이미 배송중/완료인 건을 지연으로 돌리는 건 말이 안 되므로 막아두기
    public void delay(String reason, LocalDate newExpectedShipDate) {
        if (this.status != ShippingStatus.PREPARING) {
            throw new BusinessException(ErrorCode.CANNOT_DELAY_SHIPPING);
        }
        this.status = ShippingStatus.DELAYED;
        this.delayReason = reason;
        this.expectedShipDate = newExpectedShipDate;
    }

    // ★ 배송 시작 ─ PREPARING / DELAYED 상태에서만 가능
    public void ship() {
        if (this.status != ShippingStatus.PREPARING && this.status != ShippingStatus.DELAYED) {
            throw new BusinessException(ErrorCode.CANNOT_START_SHIPPING);
        }
        this.status = ShippingStatus.SHIPPING;
        this.shippedAt = LocalDateTime.now();
    }


    // ★ 배송 완료 ─ SHIPPING 상태에서만 가능
    public void deliver() {
        if (this.status != ShippingStatus.SHIPPING) {
            throw new BusinessException(ErrorCode.CANNOT_COMPLETE_DELIVERY);
        }
        this.status = ShippingStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    // NOTE : 상태 전이 규칙을 Shipping 스스로 갖게 한 이유
    // Order.cancel() → "PENDING/PAID일 때만 취소 가능"을 스스로 아는 것과 동일한 원칙
    // 서비스 레이어에서 해당 if문을 매번 반복하면,
    // 나중에 규칙이 바뀔 때 호출부 전부를 찾아 고쳐줘야 함!
    // └ 엔티티가 규칙을 가지고 있는 경우 한 곳만 고치면 됨.

}

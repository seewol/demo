package com.jeeeun.kama.domain.order;

import com.jeeeun.kama.common.error.BusinessException;
import com.jeeeun.kama.common.error.ErrorCode;
import com.jeeeun.kama.common.jpa.BaseTimeEntity;
import com.jeeeun.kama.domain.product.ProductVariant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"order", "productVariant"})
@Entity
@Table(name = "order_item")
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    // 주문 시점 상품명 (스냅샷)
    @Column(name = "product_name", nullable = false)
    private String productName;

    // 주문 시점 조합명 (스냅샷)
    @Column(name = "product_variant_name", nullable = false)
    private String productVariantName;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    // 주문 시점 판매가 (이후 상품 가격 변동 있어도 이 값은 고정)
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    // 주문 시점 옵션 추가금
    // unitPrice에 하반되어 있지만, 정가/옵션가/할인가 분리 노출을 위해 별도로 필요함.
    @Column(name = "additional_price", nullable = false)
    private BigDecimal additionalPrice;

    // 할인 적용 후 실제 결제한 금액 (할인 없으면 unitPrice랑 동일)
    @Column(name = "discounted_price", nullable = false)
    private BigDecimal discountedPrice;

    // 주문 시점 상품 대표 이미지 URL (스냅샷)
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // 아이템 상태 (default = ORDERED, 부분 취소 시 CANCELLED로 변경)
    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false)
    private OrderItemStatus status;

    // 취소된 수량 (누적) → 부분취소가 여러 번 일어날 수 있으므로 계속 더해짐
    @Column(name ="cancelled_quantity", nullable = false)
    private long cancelledQuantity;


    public static OrderItem from(
            Order order, ProductVariant variant, long quantity,
            String productName, String productVariantName,
            BigDecimal unitPrice, BigDecimal additionalPrice, BigDecimal discountedPrice, String thumbnailUrl
    ) {
        OrderItem item = new OrderItem();
        item.order = order;
        item.productVariant = variant;
        item.quantity = quantity;
        item.productName = productName;
        item.productVariantName = productVariantName;
        item.unitPrice = unitPrice;
        item.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        item.discountedPrice = discountedPrice;
        item.thumbnailUrl = thumbnailUrl;
        item.status = OrderItemStatus.ORDERED;
        item.cancelledQuantity = 0L;

        return item;
    }


    // ★ 아이템 부분(수량 단위) 취소 ─ 이미 취소된 아이템은 재취소 불가
    public void cancel(long cancelQuantity) {

        if (this.status == OrderItemStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELLED_ORDER_ITEM);
        }

        long remainingQuantity = this.quantity - cancelledQuantity;

        // cancelQuantity : 이번에 취소할 수량. 남은 수량을 넘거나 0 이하면 예외
        if (cancelQuantity <= 0 || cancelQuantity > remainingQuantity) {
            throw new BusinessException(ErrorCode.INVALID_CANCEL_QUANTITY);
        }

        this.cancelledQuantity += cancelQuantity;

        // 취소 후, '누적 취소 수량 = 전체 수량'이면 CANCELLED, 아니면 PARTIALLY_CANCELLED
        this.status = (this.cancelledQuantity == this.quantity)
                ? OrderItemStatus.CANCELLED
                : OrderItemStatus.PARTIALLY_CANCELLED;
    }

}

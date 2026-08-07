package com.jeeeun.demo.domain.order;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.product.ProductVariant;
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


    public static OrderItem from(
            Order order, ProductVariant variant, long quantity,
            String productName, String productVariantName,
            BigDecimal unitPrice, BigDecimal discountedPrice, String thumbnailUrl
    ) {
        OrderItem item = new OrderItem();
        item.order = order;
        item.productVariant = variant;
        item.quantity = quantity;
        item.productName = productName;
        item.productVariantName = productVariantName;
        item.unitPrice = unitPrice;
        item.discountedPrice = discountedPrice;
        item.thumbnailUrl = thumbnailUrl;
        item.status = OrderItemStatus.ORDERED;

        return item;
    }


    // ★ 아이템 단위 취소 ─ 이미 취소된 아이템은 재취소 불가
    public void cancel() {
        if (this.status == OrderItemStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELLED_ORDER_ITEM);
        }
        this.status = OrderItemStatus.CANCELLED;
    }

}

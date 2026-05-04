package com.jeeeun.demo.domain.order;

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

    @Column(name = "quantity", nullable = false)
    private long quantity;

    // 주문 시점 판매가 (이후 상품 가격 변동 있어도 이 값은 고정)
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    // 할인 적용 후 실제 결제한 금액 (할인 없으면 unitPrice랑 동일)
    @Column(name = "discounted_price", nullable = false)
    private BigDecimal discountedPrice;

    public static OrderItem from(
            Order order, ProductVariant variant, long quantity,
            BigDecimal unitPrice, BigDecimal discountedPrice
    ) {
        OrderItem item = new OrderItem();
        item.order = order;
        item.productVariant = variant;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        item.discountedPrice = discountedPrice;
        return item;
    }

}

package com.jeeeun.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    // Many To One : 다대일 (N:1)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 지연로딩이란?
    // product.getCategory() 호출 전까지 카테고리 쿼리 안 날림.
    // 불필요한 조인을 피할 수 있음이 장점

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_content", nullable = false)
    private String productContent;

    // 정가 (할인 전 원가)
    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice; // 가격은 BigDecimal 사용

    // 판매가 (할인 적용 후 실제 판매가)
    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

    // 할인 여부
    @Column(name = "is_discounted", nullable = false)
    private boolean isDiscounted = false;

    // 할인율 (%)
    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "discount_start_at")
    private LocalDateTime discountStartAt;

    @Column(name = "discount_end_at")
    private LocalDateTime discountEndAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

}

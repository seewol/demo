package com.jeeeun.demo.domain;

import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"product", "optionDetail1", "optionDetail2",
        "optionDetail3", "productStocks"})
@Table(name = "product_variant")
public class ProductVariant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_variant_id", nullable = false)
    private Integer productVariantId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 옵션 디테일은 최대 3 종류
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_detail_1_id")
    private ProductOptionDetail optionDetail1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_detail_2_id")
    private ProductOptionDetail optionDetail2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_detail_3_id")
    private ProductOptionDetail optionDetail3;

    @Column(name = "variant_name", nullable = false)
    private String variantName;

    // 옵션 선택 시 추가 금액이기 때문에 nullable
    @Column(name = "additional_price")
    private BigDecimal additionalPrice;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "productVariant")
    private List<ProductStock> productStocks = new ArrayList<>();

}

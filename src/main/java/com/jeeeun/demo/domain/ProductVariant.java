package com.jeeeun.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product_variant")
public class ProductVariant {

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

    // additional_price 부터..
}

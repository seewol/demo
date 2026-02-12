package com.jeeeun.demo.domain.product;

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
@ToString(exclude = {"product", "optionDetail1", "optionDetail2",
        "optionDetail3"})
@Table(name = "product_variant")
public class ProductVariant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_variant_id", nullable = false)
    private Integer id;

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

    // 조합명 (서버에서 저장하고, 요청으로 안 받음)
    @Column(name = "variant_name", nullable = false)
    private String variantName;

    // 옵션 선택 시 추가 금액이기 때문에 nullable
    @Column(name = "additional_price")
    private BigDecimal additionalPrice;

}

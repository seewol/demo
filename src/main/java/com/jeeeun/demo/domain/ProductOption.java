package com.jeeeun.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"product", "productOptionDetails"})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product_option")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id", nullable = false)
    private Integer optionId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 옵션 설명 (ex. color, size)
    @Column(name = "option_name", nullable = false)
    private String optionName;

    @OneToMany(mappedBy = "productOption")
    private List<ProductOptionDetail> productOptionDetails = new ArrayList<>();

}

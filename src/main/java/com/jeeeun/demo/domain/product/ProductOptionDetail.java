package com.jeeeun.demo.domain.product;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"productOption"})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product_option_detail")
public class ProductOptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_detail_id")
    private Integer id;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private ProductOption productOption;

    // 옵션 내용 (ex. 옵션 설명 color 안의 blue, silver..)
    @Column(name = "option_content", nullable = false, length = 100)
    private String description;

}

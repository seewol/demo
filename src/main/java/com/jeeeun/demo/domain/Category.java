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
@ToString(exclude = {"products"})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    // 연관 관계에서는 FK가 있는 쪽이 주인
    // 여기서는 Product가 FK(category_id)를 가짐
    // 고로 Product가 주인 = Product.category

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    // 반대로 Category.products는 읽기 전용 뷰 같은 것.
    // 그렇기 때문에 mappedBy 사용.

    @Column(name = "category_name", nullable = false)
    private String categoryName;

}

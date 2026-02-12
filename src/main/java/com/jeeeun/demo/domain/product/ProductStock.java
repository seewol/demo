package com.jeeeun.demo.domain.product;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_stock",
    uniqueConstraints = {   // 해당 테이블에서 product_variant_id 컬럼 값은 중복 X
        @UniqueConstraint(
                name = "uk_product_stock_variant", // DB 내 제약 조건 이름
                columnNames = "product_variant_id" // 같은 variant 로 행 2개 못 만들게!
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"productVariant", "productVariant"})
public class ProductStock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id", nullable = false)
    private Integer id;

    // 단방향
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(name = "stock_quantity", nullable = false)
    private long quantity;

    /*
        아직 존재하지 않는 객체를 만들기 위한 메서드는 static
        = 어떤 인스턴스에도 속하지 않은 생성 규칙
    */

    public static ProductStock create(ProductVariant variant, int initialQuantity) {
                                                                    // 초기 수량
        if (initialQuantity < 0) {
            throw new BusinessException(ErrorCode.INVALID_STOCK_QUANTITY);
        }

        ProductStock stock = new ProductStock();
        stock.productVariant = variant;
        stock.quantity = initialQuantity;
        return stock;
    }

    public void setQuantity(long quantity) {
        if (quantity < 0) {
            throw new BusinessException(ErrorCode.INVALID_STOCK_QUANTITY);
        }
        this.quantity = quantity;
    }

    public void increase(long quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_STOCK_QUANTITY);
        }
        this.quantity += quantity;
    }

    public void decrease(long quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_STOCK_QUANTITY);
        }
        if (this.quantity - quantity < 0) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }
        this.quantity -= quantity;
    }
}

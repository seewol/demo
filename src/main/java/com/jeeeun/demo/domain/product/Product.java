package com.jeeeun.demo.domain.product;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.common.jpa.BaseTimeEntity;
import com.jeeeun.demo.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"category", "productImages", "productOptions", "productVariants"})
@Entity
@Table(name = "product")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    // Many To One : 다대일 (N:1)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 지연로딩이란?
    // product.getCategory() 호출 전까지 카테고리 쿼리 안 날림.
    // 불필요한 조인을 피할 수 있음이 장점

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_content", nullable = false)
    private String description;

    // 판매가 (할인 적용 후 실제 판매가)
    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

//    // 정가 (할인 전 원가)
//    @Column(name = "original_price", nullable = false)
//    private BigDecimal originalPrice; // 가격은 BigDecimal 사용

    // 할인 여부
    @Builder.Default
    @Column(name = "is_discounted", nullable = false)
    private boolean isDiscounted = false;

    // 할인율 (%)
    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "discount_start_at")
    private LocalDateTime discountStartAt;

    @Column(name = "discount_end_at")
    private LocalDateTime discountEndAt;

    // 1인당 최대 구매 수량 (null → 제한 없음)
    // ex) exclusive 상품 → maxPurchaseQuantity = 1
    @Column(name = "max_purchase_quantity")
    private Integer maxPurchaseQuantity;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // mappedBy ?
    // = 상대 엔티티에서 이 관계를 담당하고 있는 필드명 기재
    // mappedBy 가 붙은 쪽은 연관관계 주인이 아님

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<ProductOption> productOptions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<ProductVariant> productVariants = new ArrayList<>();


    // ★ 상품 기본 정보 수정 (비즈니스 규칙 3가지 검증)
    public void updateProduct(
            Category category,
            String name,
            String description,
            BigDecimal salePrice,
            Boolean isDiscounted,
            Integer discountRate,
            LocalDateTime discountStartAt,
            LocalDateTime discountEndAt,
            Integer maxPurchaseQuantity
    ) {
        // ★ 1: 삭제된 상품 수정 불가
        if (this.isDeleted) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED_PRODUCT);
        }

        // null 아닌 필드만 수정 (PATCH 방식)
        if (category != null) {
            this.category = category;
        }
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (salePrice != null) {
            this.salePrice = salePrice;
        }
        if (maxPurchaseQuantity != null) {
            this.maxPurchaseQuantity = maxPurchaseQuantity;
        }

        // ★ 할인 관련 필드는 묶어서 처리
        // 일단 isDiscounted가 넘어왔으면 할인 상태 변경할 의도가 있는 것
        if (isDiscounted != null) {
            this.isDiscounted = isDiscounted;

            if (isDiscounted) {

                // ★ 2: 할인 적용 시 '할인율' 필수
                Integer finalRate = (discountRate != null) ? discountRate : this.discountRate;
                if (finalRate == null) {
                    throw new BusinessException(ErrorCode.INVALID_DISCOUNT_RATE);
                }
                this.discountRate = finalRate;

                // ★ 3: 할인 적용 시 '할인 기간' 필수
                LocalDateTime finalStart = (discountStartAt != null) ? discountStartAt : this.discountStartAt;
                LocalDateTime finalEnd = (discountEndAt != null) ? discountEndAt : this.discountEndAt;
                if (finalStart == null || finalEnd == null) {
                    throw new BusinessException(ErrorCode.INVALID_DISCOUNT_PERIOD);
                }
                // 종료일이 시작일보다 이전일 경우 예외
                if (finalStart.isAfter(finalEnd)) {
                    throw new BusinessException(ErrorCode.INVALID_DISCOUNT_PERIOD);
                }
                this.discountStartAt = finalStart;
                this.discountEndAt = finalEnd;

            } else {

                // 할인 해제 (isDiscounted = false)
                // 할인 관련 필드 모두 초기화
                this.discountRate = null;
                this.discountStartAt = null;
                this.discountEndAt = null;

            }

        } else {
            // isDiscounted 안 보낸 경우 → 할인율 / 기간만 개별적으로 수정 가능
            if (discountRate != null) {
                this.discountRate = discountRate;
            }
            if (discountStartAt != null) {
                this.discountStartAt = discountStartAt;
            }
            if (discountEndAt != null) {
                this.discountEndAt = discountEndAt;
            }
        }

    }


    // ★ 상품 삭제 (soft delete)
    public void delete() {
        // 이미 삭제됐으면 예외, 아니면 삭제
        if (this.isDeleted) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED_PRODUCT);
        }
        this.isDeleted = true;
    }


}

















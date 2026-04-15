package com.jeeeun.demo.controller.response;

import com.jeeeun.demo.domain.user.Role;
import com.jeeeun.demo.service.user.model.UserDetailResult;
//import com.jeeeun.demo.service.user.model.ProductSummaryResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UserDetailResponse(

    // 응답 DTO 에서는 @Valid 안 달아도 됨
    Long id,
    String name,
    String email,
    String phoneNumber,
    Role role,
    ProductSummary productSummary,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

    @Builder
    public record ProductSummary (
        Long productId,
        String productName,
        BigDecimal salePrice,       // 실제 판매가 (노출가)
        boolean isDiscounted,       // 할인 여부(boolean은 null X)
        Integer discountRate       // 할인율
    ) {}

    public static UserDetailResponse from(UserDetailResult result) {

//        ProductSummaryResult ps = result.productSummary();

        return UserDetailResponse.builder()
                .id(result.id())
                .name(result.name())
                .email(result.email())
                .phoneNumber(result.phoneNumber())
                .role(result.role())
//                .productSummary(
//                        ps == null ? null :
//                                ProductSummary.builder()
//                                        .productId(ps.productId())
//                                        .productName(ps.productName())
//                                        .salePrice(ps.salePrice())
//                                        .isDiscounted(ps.isDiscounted())
//                                        .discountRate(ps.discountRate())
//                                        .build()
//                )
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();
    }
}

    /* ProductSummary 는?
    진짜 테이블이 아닌 응답 DTO 안의 중첩 클래스.
    특정 API 응답에서만 쓰는 구조로, 직접 매핑해 채워줘야 한다.

    ex) 회원 상세 정보 안에서 보여줄 상품 요약 정도?
    → 단지 JSON 으로 변환될 view 전용 객체.

    결론! 이렇게 '특정 API 응답에서만 쓰는 구조'는
    → inner static class 로 두어 응집도를 높이는 게 깔끔하다.
    */

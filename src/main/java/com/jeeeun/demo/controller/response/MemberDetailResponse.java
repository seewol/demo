package com.jeeeun.demo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailResponse {
    Integer memberId;
    String memberName;
    String memberEmail;
    String phoneNumber;
    ProductSummary productSummary;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductSummary {
        Integer productId;
        String productName;
        BigDecimal originalPrice;   // 정가
        BigDecimal salePrice;       // 실제 판매가 (노출가)
        boolean isDiscounted;       // 할인 여부(boolean은 null X)
        Integer discountRate;       // 할인율
    }

    /* ProductSummary 는?
    진짜 테이블이 아닌 응답 DTO 안의 중첩 클래스.
    특정 API 응답에서만 쓰는 구조로, 직접 매핑해 채워줘야 한다.

    ex) 회원 상세 정보 안에서 보여줄 상품 요약 정도?
    → 단지 JSON 으로 변환될 view 전용 객체.

    결론! 이렇게 '특정 API 응답에서만 쓰는 구조'는
    → inner static class 로 두어 응집도를 높이는 게 깔끔하다.
    */

}
package com.jeeeun.demo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    ProductDetailResponse product;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetailResponse {
        Integer productId;
        String productName;
    }
}
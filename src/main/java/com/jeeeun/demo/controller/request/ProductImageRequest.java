package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageRequest {

    @NotBlank
    private String imageUrl;

    @NotNull
    private Integer imageOrder;
}

/* ★ ★ ★ ★ ★

JSON 요청
   ↓
ProductCreateRequest (DTO)
   ↓
Controller (요청 수신, 데이터를 받는 단순 통로 역할)
   ↓
Service (DTO → Entity 매핑)
   ↓
Repository (DB 저장)
   ↓
Product, ProductImage, ProductOption, ProductVariant (엔티티 계층)

∴ DTO: 데이터 운반용, Entity: DB 매핑용, Service: 연결자

★ ★ ★ ★ ★ */
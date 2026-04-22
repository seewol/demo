package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.ProductCreateRequest;
import com.jeeeun.demo.controller.request.ProductVariantCreateRequest;
import com.jeeeun.demo.controller.request.StockUpdateRequest;
import com.jeeeun.demo.controller.response.*;
import com.jeeeun.demo.service.ProductCommandService;
import com.jeeeun.demo.service.ProductQueryService;
import com.jeeeun.demo.service.product.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
// @Controller + @ResponseBody 합쳐진 것
// 메서드 반환값을 그대로 HTTP 응답 바디(JSON/문자열)로 내려줌.
// └ Jackson : 객체 → JSON 변환
@Tag(name = "ProductController", description = "Product CRUD API 엔드포인트")
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    // responseCode
    // 200 : 요청 성공, 201 : 서버에 새로운 리소스 생성 성공


    // 상품 등록 (C)
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "상품 등록 성공")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products")
    public ProductCreateResponse createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        return ProductCreateResponse.from(productCommandService.createProduct(request.toCommand()));
    }


    // 상품 목록 조회 (R)
    @Operation(summary = "상품 목록 조회", description = "상품 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/products")
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) String keyword, // required = false → null 허용
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isDiscounted,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
                            // @PageableDefault → ?page=0&size=20 안 보내도 동작
    ) {
        return productQueryService.getProducts(keyword, categoryId, isDiscounted, pageable)
                .map(ProductResponse::from);    // 각 ProductResult → ProductResponse 변환
    }


    // 단일 상품 조회 (상품 상세 조회)
    @Operation(summary = "단일 상품 조회", description = "상품 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/products/{productId}")
    public ProductDetailResponse getProduct(
            @PathVariable Long productId
    ) {
        ProductDetailResult result  = productQueryService.getProductDetail(productId);

        return ProductDetailResponse.from(result);
    }


    // 상품 조합 등록 (C)
    @Operation(summary = "상품 조합 등록", description = "상품 조합을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products/{productId}/variants")
    public ProductVariantCreateResponse createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantCreateRequest request
    ) {
        request.validate(); // 입력 값에 대한 검증을 한 후, 서비스로 넘기기

        ProductVariantCreateResult result
                = productCommandService.createVariant(request.toCommand(productId));

        return ProductVariantCreateResponse.from(result);
    }


    @Operation(summary = "상품 재고 업데이트", description = "상품 재고를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/variants/{variantId}/stock")
    public StockUpdateResponse updateStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        StockUpdateResult result =
                productCommandService.updateStock(request.toCommand(variantId));

        return StockUpdateResponse.from(result);
    }


    /*
        Collection은 가장 상위 타입
        List, Set, Queue 등 모든 컬렉션의 공통 조상
        중복 여부를 보장하지 않음
    */


    /*
        ResponseEntity : HTTP 응답을 '객체'로 표현
        → 상태코드, 헤더, 바디 한 번에 컨트롤 가능
        (스프링 프레임워크가 제공하는 응답 컨테이너)

        [ex]
        return ResponseEntity.ok(body);                      // 200 OK + 바디
        return ResponseEntity.status(201).body(body);        // 201 Created + 바디
        return ResponseEntity.noContent().build();           // 204 No Content
        return ResponseEntity.created(location).body(body);  // 201 + Location 헤더 + 바디
     */


    /*
        엔드 포인트 : 서버가 제공하는 특정 기능에 접근하는 주소
        구성 : 보통 HTTP 메서드 + 경로(Path)조합
        [ex] GET /products/1 (상품1 상세 조회), POST /products (상품 등록)
        ▷ 참고로, 쿼리스트링(?page=1)이나 헤더까지 포함해 더 좁게 말하기도 하나,
        실무에서는 메서드 + 경로 정도로 식별하면 충분!
    */

}



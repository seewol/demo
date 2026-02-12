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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
// @Controller + @ResponseBody 합쳐진 것
// 메서드 반환값을 그대로 HTTP 응답 바디(JSON/문자열)로 내려줌.
// └ Jackson : 객체 → JSON 변환
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    // responseCode
    // 200 : 요청 성공, 201 : 서버에 새로운 리소스 생성 성공


    // 상품 등록 (C)
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "상품 등록 성공")
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
    public List<ProductResponse> getProducts() {

        List<ProductResult> results =  productQueryService.getProducts();

        // List<ProductResult> → List<ProductResponse> 변환 과정
        return results.stream()
                .map(ProductResponse::from)
                .toList();
    }


    // 단일 상품 조회 (상품 상세 조회)
    @Operation(summary = "단일 상품 조회", description = "상품 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/products/{productId}")
    public ProductDetailResponse getProduct(
            @PathVariable Integer productId
    ) {
        ProductDetailResult result  = productQueryService.getProductDetail(productId);

        return ProductDetailResponse.from(result);
    }


    // 상품 조합 등록 (C)
    @Operation(summary = "상품 조합 등록", description = "상품 조합을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @PostMapping("/products/{productId}/variants")
    public ProductVariantCreateResponse createVariant(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductVariantCreateRequest request
    ) {
        request.validate(); // 입력 값에 대한 검증을 한 후, 서비스로 넘기기

        ProductVariantCreateResult result
                = productCommandService.createVariant(request.toCommand(productId));

        return ProductVariantCreateResponse.from(result);
    }


    @Operation(summary = "상품 재고 업데이트", description = "상품 재고를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/variants/{variantId}/stock")
    public StockUpdateResponse updateStock(
            @PathVariable Integer variantId,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        StockUpdateResult result =
                productCommandService.updateStock(request.toCommand(variantId));

        return StockUpdateResponse.from(result);
    }

    // product PUT
    // 상품 수정
//    @PutMapping("/products/{productId}")
//    public ResponseEntity<String> updateProduct(
//            @RequestBody ProductRequest request,
//            @PathVariable Integer productId
//    ) {
//        ProductResponse updated = STORE.get(productId);
//
//        if (updated != null) {
//            request.applyTo(updated);
//            // 사용자한테 받은 요청을 기존 데이터 'updated' 에 반영.
//        } else {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "수정할 상품이 없습니다."
//            );
//        }
//
//        return ResponseEntity.ok("상품 수정 성공 [id = " + productId + "]");
//    }

    // product DELETE
    // 상품 삭제
//    @DeleteMapping("/products/{productId}")
//    public ResponseEntity<String> deleteProduct(
//            @PathVariable Integer productId
//    ) {
//        ProductResponse deleted = STORE.remove(productId);
//
//        if (deleted == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "삭제할 상품이 없습니다."
//            );
//        }
//
//        return ResponseEntity.ok("상품 삭제 성공 [id = " + productId + "]");
//    }


    //    // 상품 목록 조회
//    @GetMapping("/products")
//    public List<ProductResponse> getProducts() {
//        return List.of(
//                new ProductResponse(
//                        1,
//                        "Product A",
//                        100.0,
//                        "상품 1 설명",
//                        "http://example.com/image1",
//                        LocalDateTime.parse("2025-08-25T22:00:00")
//                        ),
//                new ProductResponse(
//                        2,
//                        "Product B",
//                        150.0,
//                        "상품 2 설명",
//                        "http://example.com/image2",
//                        LocalDateTime.parse("2025-08-26T21:00:00")
//                )
//        );
//    }


    // product GET
    // 상품 목록 조회
//    @GetMapping("/products")
//    public Collection<ProductResponse> getProducts() {
//        return STORE.values();
//    }

    // .values() : 맵에 저장된 값(value)만 꺼내 Collection 인터페이스 형식으로 반환

    /*
        Map은 Collection의 하위 타입이 아니므로,
        .values()는 Collection을 돌려주지만 "그건 맵의 값 뷰"

        .keySet() : 모든 키만 반환
        .entrySet() :  키, 값 쌍을 한 번에 반환
        └ entrySet()의 반환 타입은 Set<Map.Entry<K, V>>
        └ 왜냐하면, Map에서 key는 중복될 수 없으니 받으시 Set 컬렉션이 맞음!
    */

    /*
        Collection은 가장 상위 타입
        List, Set, Queue 등 모든 컬렉션의 공통 조상
        중복 여부를 보장하지 않음
    */


    // product GET
    // 상품 상세 조회
//    @GetMapping("/products/{productId}")
//    public ProductResponse getProduct(
//            @PathVariable("productId") Integer productId
//    ) {
//        ProductResponse productResponse = STORE.get(productId);
//
//        // 키 없으면 null 반환
//        if (productResponse == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "조회된 상품이 없습니다."
//            );
//        }
//
//        return productResponse;
//    }


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


    // 임시 저장소
    // static이기 때문에 애플리케이션 살아있는 동안 공유, 서버 재시작 시 초기화
//    private static final HashMap<Integer, ProductResponse> STORE = new HashMap<>();
    // 동시 요청이 많을 경우 ConcurrentHashMap 고려하라는데 모르겠음.
    // ConcurrentHashMap → ConcurrentMap → Map을 구현
    // Map → Collection 상속하지 않음! (둘은 자바 컬렉션 프레임워크 내 형제 느낌)

    // id 시퀀스 흉내
//    private static Integer seq = 2;
    // 현재는 단일 스레드 테스트 용으로 적합
    // 동시성 고려하려면 AtomicInteger 사용이 안전하다는데 모르겠음.

    // 미리 넣어둔 상품
    // └ 서버 실행 시 미리 저장
//    static {
//        STORE.put(1, new ProductResponse(
//                1, "Product A", 100.0, "상품 1 설명",
//                "http://example.com/image1",
//                LocalDateTime.parse("2025-08-25T22:00:00"),
//                LocalDateTime.parse("2025-08-25T22:00:00")
//        ));
//        STORE.put(2, new ProductResponse(
//                2, "Product B", 300.0, "상품 2 설명",
//                "http://example.com/image2",
//                LocalDateTime.parse("2025-08-26T10:00:00"),
//                LocalDateTime.parse("2025-08-26T10:00:00")
//        ));
//    }


    /*
        엔드 포인트 : 서버가 제공하는 특정 기능에 접근하는 주소
        구성 : 보통 HTTP 메서드 + 경로(Path)조합
        [ex] GET /products/1 (상품1 상세 조회), POST /products (상품 등록)
        ▷ 참고로, 쿼리스트링(?page=1)이나 헤더까지 포함해 더 좁게 말하기도 하나,
        실무에서는 메서드 + 경로 정도로 식별하면 충분!
    */

}



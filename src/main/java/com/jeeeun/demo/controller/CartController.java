package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.CartItemCreateRequest;
import com.jeeeun.demo.controller.request.CartItemUpdateRequest;
import com.jeeeun.demo.controller.request.CartMergeRequest;
import com.jeeeun.demo.controller.response.CartItemCreateResponse;
import com.jeeeun.demo.controller.response.CartItemUpdateResponse;
import com.jeeeun.demo.controller.response.CartMergeResponse;
import com.jeeeun.demo.controller.response.CartResponse;
import com.jeeeun.demo.service.CartCommandService;
import com.jeeeun.demo.service.CartQueryService;
import com.jeeeun.demo.service.cart.model.CartItemCreateResult;
import com.jeeeun.demo.service.cart.model.CartItemUpdateResult;
import com.jeeeun.demo.service.cart.model.CartMergeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "CartController", description = "Cart CRUD API 엔드포인트")
@RequestMapping("/cart") // Cart 관련 API 모두 "/cart"로 시작
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    @Operation(summary = "장바구니 아이템 추가")
    @ApiResponse(responseCode = "201", description = "추가 성공")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public CartItemCreateResponse addCartItem(
            @Valid @RequestBody CartItemCreateRequest request
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        CartItemCreateResult result = cartCommandService.addCartItem(request.toCommand(userId));

        return CartItemCreateResponse.from(result);
    }

    @Operation(summary = "장바구니 아이템 수량 변경")
    @ApiResponse(responseCode = "200", description = "수량 변경 성공")
    @PatchMapping("/items/{cartItemId}")
    public CartItemUpdateResponse updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        CartItemUpdateResult result = cartCommandService.updateCartItemQuantity(
                request.toCommand(userId, cartItemId));

        return CartItemUpdateResponse.from(result);
    }

    @Operation(summary = "내 장바구니 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public CartResponse getCart() {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return CartResponse.from(cartQueryService.getCart(userId));
    }

    @Operation(summary = "장바구니 아이템 삭제")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/items/{cartItemId}")
    public void deleteCartItem(
            @PathVariable Long cartItemId
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        cartCommandService.deleteCartItem(userId, cartItemId);
    }

    @Operation(summary = "비로그인 장바구니 머지", description = "로그인 후, 로컬스토리지에 담았던 장바구니를 서버 장바구니와 합칩니다.")
    @ApiResponse(responseCode = "200", description = "merge 성공")
    @PostMapping("/merge")
    public CartMergeResponse mergeCart(
            @Valid @RequestBody CartMergeRequest request
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        CartMergeResult result = cartCommandService.mergeCart(request.toCommand(userId));

        return CartMergeResponse.from(result);
    }
}

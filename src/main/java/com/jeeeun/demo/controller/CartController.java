package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.CartItemCreateRequest;
import com.jeeeun.demo.controller.response.CartItemCreateResponse;
import com.jeeeun.demo.service.CartCommandService;
import com.jeeeun.demo.service.cart.model.CartItemCreateResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "CartController", description = "Cart CRUD API 엔드포인트")
@RequestMapping("/cart") // Cart 관련 API 모두 /cart 시작
public class CartController {

    private final CartCommandService cartCommandService;

    @Operation(description = "장바구니 아이템 추가")
    @ApiResponse(responseCode = "201", description = "추가 성공")
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
}

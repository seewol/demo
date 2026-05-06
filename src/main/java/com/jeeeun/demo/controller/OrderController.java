package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.OrderCreateRequest;
import com.jeeeun.demo.controller.response.OrderCreateResponse;
import com.jeeeun.demo.service.OrderCommandService;
import com.jeeeun.demo.service.order.model.OrderCreateResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
@Tag(name = "OrderController", description = "Order API 엔드포인트")
public class OrderController {

    private final OrderCommandService orderCommandService;

    // ★ 주문 생성 (C)
    @Operation(description = "주문 생성")
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderCreateResponse createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        OrderCreateResult result = orderCommandService.createOrder(request.toCommand(userId));

        return OrderCreateResponse.from(result);
    }

//
//    // ★ 내 주문 목록 조회
//    // GET /orders
//    @Operation(description = "주문 목록 조회")
//    @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공")
//    @GetMapping
//    public List<OrderResponse> getOrders() {
//
//        Long userId = (Long) SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return null;
//    }
//
//
//    // ★ 내 주문 상세 조회
//    // GET /orders/{orderId}
//    @Operation(description = "주문 상세 조회")
//    @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공")
//    @GetMapping("/{orderId}")
//    public OrderDetailResponse getOrder(
//            @PathVariable Long orderId
//    ) {
//
//        Long userId = (Long) SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return null;
//    }
//
//    // ★ 주문 취소
//    // PATCH /orders/{orderId}/cancel
//    @Operation(description = "주문 취소")
//    @ApiResponse(responseCode = "200", description = "주문 취소 성공")
//    @PatchMapping("/{orderId}/cancel")
//    public OrderCancelResponse cancelOrder(
//            @PathVariable Long orderId
//    ) {
//
//        Long userId = (Long) SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return null;
//    }

}

package com.jeeeun.kama.controller;

import com.jeeeun.kama.controller.request.DirectOrderCreateRequest;
import com.jeeeun.kama.controller.request.OrderCancelRequest;
import com.jeeeun.kama.controller.request.OrderCreateRequest;
import com.jeeeun.kama.controller.request.OrderItemCancelRequest;
import com.jeeeun.kama.controller.response.*;
import com.jeeeun.kama.repository.order.OrderRepository;
import com.jeeeun.kama.service.OrderCommandService;
import com.jeeeun.kama.service.OrderQueryService;
import com.jeeeun.kama.service.order.model.OrderCancelResult;
import com.jeeeun.kama.service.order.model.OrderCreateResult;
import com.jeeeun.kama.service.order.model.OrderItemCancelResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
@Tag(name = "OrderController", description = "Order API 엔드포인트")
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final OrderRepository orderRepository;

    // ★ 주문 생성 - 장바구니 (C)
    @Operation(summary = "주문 생성")
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


    // ★ 주문 생성 - 바로구매 (C)
    @Operation(summary = "바로구매", description = "장바구니 거치지 않는 주문입니다.")
    @ApiResponse(responseCode = "201", description = "바로구매 성공")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/direct")
    public OrderCreateResponse createDirectOrder(
            @Valid @RequestBody DirectOrderCreateRequest request
    ) {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        OrderCreateResult result = orderCommandService.createDirectOrder(
                request.toCommand(userId));

        return OrderCreateResponse.from(result);
    }


    // ★ 내 주문 목록 조회
    // GET /orders
    @Operation(summary = "주문 목록 조회")
    @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공")
    @GetMapping
    public Page<OrderResponse> getOrders(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return orderQueryService.getOrders(userId, pageable)
                .map(OrderResponse::from);
    }


    // ★ 내 주문 상세 조회
    // GET /orders/{orderId}
    @Operation(summary = "주문 상세 조회")
    @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공")
    @GetMapping("/{orderId}")
    public OrderDetailResponse getOrder(
            @PathVariable Long orderId
    ) {

        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return OrderDetailResponse.from(orderQueryService.getOrder(userId, orderId));
    }


    // ★ 주문 취소
    // PATCH /orders/{orderId}/cancel
    @Operation(summary = "주문 취소", description = "주문 취소 후 재고를 복구합니다.")
    @ApiResponse(responseCode = "200", description = "주문 취소 성공")
    @PatchMapping("/{orderId}/cancel")
    public OrderCancelResponse cancelOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderCancelRequest request
            ) {

        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        OrderCancelResult result = orderCommandService.cancelOrder(request.toCommand(orderId, userId));

        return OrderCancelResponse.from(result);
    }


    // ★ 주문 아이템 단위 부분 취소 (수량 단위)
    @Operation(summary = "주문 아이템 부분 취소", description = "지정 수량만큼 취소 후, 해당 재고를 복구합니다.")
    @ApiResponse(responseCode = "200", description = "주문 아이템 취소 성공")
    @PatchMapping("/{orderId}/items/{orderItemId}/cancel")
    public OrderItemCancelResponse cancelOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId,
            @Valid @RequestBody OrderItemCancelRequest request
    ) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        OrderItemCancelResult result = orderCommandService.cancelOrderItem(
                request.toCommand(orderId, orderItemId, userId));

        return OrderItemCancelResponse.from(result);
    }

}

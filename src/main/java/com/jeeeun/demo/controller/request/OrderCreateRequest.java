package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.order.model.OrderCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateRequest(

        // ★ 주문할 장바구니 속 아이템 ID 목록
        // NOTE : 최소 1개 이상 아이템 선택 시 주문 가능
        // null도 안 되고, 빈 리스트도 안 됨!
        @NotEmpty(message = "주문할 상품을 선택해주세요.")
        List<Long> cartItemIds,

        // 포트원이 결제 후 발급한 결제 고유번호
        // 백엔드는 이 값을 통해 포트원 서버에 결제 검증 요청
        @NotBlank(message = "결제 정보가 누락되었어요.")
        String impUid

    // @NotNull   → null만 막음
    // @NotEmpty  → null + 빈 값 막음 (List, String, 배열 등)
    // @NotBlank  → null + 빈 값 + 공백만 있는 값 막음 (String 전용)

) {
    public OrderCreateCommand toCommand(Long userId) {
        return OrderCreateCommand.builder()
                .userId(userId)
                .cartItemIds(cartItemIds)
                .impUid(impUid)
                .build();
    }
}

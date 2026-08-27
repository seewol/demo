package com.jeeeun.kama.controller;

import com.jeeeun.kama.controller.request.StockUpdateRequest;
import com.jeeeun.kama.controller.response.StockUpdateResponse;
import com.jeeeun.kama.service.ProductCommandService;
import com.jeeeun.kama.service.product.model.StockUpdateResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/variants")
@Tag(name = "재고", description = "상품 옵션(variant) 재고 관리 API")
public class VariantController {

    private final ProductCommandService productCommandService;

    @Operation(summary = "상품 재고 업데이트")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 옵션 조합입니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{variantId}/stock")
    public StockUpdateResponse updateStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        StockUpdateResult result =
                productCommandService.updateStock(request.toCommand(variantId));

        return StockUpdateResponse.from(result);
    }

}

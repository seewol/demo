package com.jeeeun.demo.controller;

import com.jeeeun.demo.controller.request.StockUpdateRequest;
import com.jeeeun.demo.controller.response.StockUpdateResponse;
import com.jeeeun.demo.service.ProductCommandService;
import com.jeeeun.demo.service.product.model.StockUpdateResult;
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
@Tag(name = "VariantController", description = "Variant API 엔드포인트")
public class VariantController {

    private final ProductCommandService productCommandService;

    @Operation(summary = "상품 재고 업데이트")
    @ApiResponse(responseCode = "200", description = "수정 성공")
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

package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.common.error.BusinessException;
import com.jeeeun.demo.common.error.ErrorCode;
import com.jeeeun.demo.service.product.model.ProductVariantCreateCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductVariantCreateRequest(

    @NotEmpty
    @Size(min = 1, max = 3) // 조합은 최소 1개 ~ 최대 3개
    List<Integer> optionDetailIds,

    BigDecimal additionalPrice

) {

    public void validate() {

        if (additionalPrice != null && additionalPrice.signum() < 0) {
            throw new IllegalArgumentException(ErrorCode.BAD_REQUEST.getMessage());
        }
        // signum() : BigDecimal 부호 (음수는 -1, 0은 0, 양수는 1)

    }

    public ProductVariantCreateCommand toCommand(Integer productId) {

        BigDecimal price = (additionalPrice == null) ? BigDecimal.ZERO : additionalPrice;

        return new ProductVariantCreateCommand(
                productId,
                optionDetailIds.stream().sorted().toList(),
                price
        );
    }
    // additionalPrice = record 의 필드 (컴포넌트)
    // └ record 내부 메서드에서는 필드 이름 그대로 써도 됨
    // additionalPrice() = record 가 자동으로 만들어주는 getter 메서드
    // └ record 외부(다른) 클래스에서는 메서드로 접근
}

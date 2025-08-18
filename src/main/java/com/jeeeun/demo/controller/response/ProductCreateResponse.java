package com.jeeeun.demo.controller.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateResponse {
    private Integer productId;
    private String message;
}

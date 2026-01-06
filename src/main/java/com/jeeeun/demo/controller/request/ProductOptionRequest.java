package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOptionRequest {

    @NotBlank
    private String optionName;

    @NotEmpty
    private List<String> optionDetails;
}

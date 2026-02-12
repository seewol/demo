package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Builder
public record ProductOptionRequest (

    @NotBlank
    String optionName,

    @NotEmpty
    List<String> optionDetails

) {}

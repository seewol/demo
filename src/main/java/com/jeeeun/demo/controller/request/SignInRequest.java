package com.jeeeun.demo.controller.request;


import jakarta.validation.constraints.NotBlank;

public record SignInRequest(

    @NotBlank
    String accessToken

) {}

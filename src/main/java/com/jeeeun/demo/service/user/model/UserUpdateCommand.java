package com.jeeeun.demo.service.user.model;

import lombok.Builder;

@Builder
public record UserUpdateCommand(

        Long userId,
        String name,
        String phoneNumber

) {}

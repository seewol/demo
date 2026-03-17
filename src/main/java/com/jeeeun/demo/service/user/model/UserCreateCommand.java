package com.jeeeun.demo.service.user.model;

import lombok.Builder;

@Builder
public record UserCreateCommand(
   String name,
   String email,
   String password,
   String phoneNumber

) {}
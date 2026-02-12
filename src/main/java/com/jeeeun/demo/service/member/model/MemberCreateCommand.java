package com.jeeeun.demo.service.member.model;

import lombok.Builder;

@Builder
public record MemberCreateCommand (
   String name,
   String email,
   String password,
   String phoneNumber

) {}
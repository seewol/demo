package com.jeeeun.demo.service.member.model;

import lombok.Builder;

@Builder
public record MemberUpdateCommand (

        Integer id,
        String name,
        String phoneNumber

) {}

package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.member.model.MemberUpdateCommand;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
public record MemberUpdateRequest (

    String name,

    @Pattern(regexp = "^(010)-?\\d{3,4}-?\\d{4}$",
            message = "휴대폰 번호 형식이 올바르지 않습니다.")
    String phoneNumber

) {
    /**
     * Request → Command 변환
     * - Controller → Service 경계용
     * - Validation 통과 후 호출되는 것을 전제로 함
     */
    public MemberUpdateCommand toCommand(Integer memberId) {

        String normalizedPhone = null;

        // 수정 요청에 phoneNumber 들어온 경우만 정규화 + 정책 검증!
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            normalizedPhone = phoneNumber.replaceAll("[^0-9]", "");

            if (!normalizedPhone.matches("^010\\d{7,8}$")) {
                throw new IllegalArgumentException("휴대폰 번호 형식이 올바르지 않습니다.");
            }
        }

        return MemberUpdateCommand.builder()
                .id(memberId)
                .name(name)
                .phoneNumber(normalizedPhone)
                .build();
    }

}

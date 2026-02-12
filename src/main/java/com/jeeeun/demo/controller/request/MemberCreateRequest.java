package com.jeeeun.demo.controller.request;

import com.jeeeun.demo.service.member.model.MemberCreateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MemberCreateRequest (

    @NotBlank(message = "이름은 필수 입력값입니다.")
    String name,

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 10, message = "비밀번호는 최소 10자 이상입니다.")
    String password,

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(010)-?\\d{3,4}-?\\d{4}$",
            message = "휴대폰 번호 형식이 올바르지 않습니다.")
    String phoneNumber

) {

    /**
     * Request → Command 변환
     * - Controller → Service 경계용
     * - Validation 통과 후 호출되는 것을 전제로 함
     */

    public MemberCreateCommand toCommand() {

        // 휴대폰 번호 형식 검증
        String normalizedPhone = phoneNumber.replaceAll("[^0-9]", "");

        if (!normalizedPhone.matches("^010\\d{7,8}$")) {
            throw new IllegalArgumentException("휴대폰 번호 형식이 올바르지 않습니다.");
        }

        return MemberCreateCommand.builder()
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(normalizedPhone)
                .build();
    }


}

package com.jeeeun.demo.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberCreateRequest {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String memberName;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식에 올바르지 않습니다.")
    private String memberEmail;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 10, message = "비밀번호는 최소 10자 이상입니다.")
    private String memberPw;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(010)-?\\d{3,4}-?\\d{4}$",
            message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

}

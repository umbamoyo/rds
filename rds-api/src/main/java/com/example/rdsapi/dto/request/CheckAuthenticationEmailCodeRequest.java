package com.example.rdsapi.dto.request;

import com.example.rdsapi.dto.EmailCodeDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record CheckAuthenticationEmailCodeRequest(
        @Email @NotBlank(message = "이메일을 입력해주세요.")
        String userId,
        @NotBlank(message = "인증코드를 입력해주세요.")
        String code
) {

    public EmailCodeDto toDto() {return EmailCodeDto.of(userId, code);}
}

package com.example.rdsapi.dto.request;

import com.example.rdsapi.dto.SignInDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record SignInRequest(
        @Email @NotBlank(message = "이메일을 입력해주세요.")
        String userId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}", message = "비밀번호는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        String password
) {

    public SignInDto toDto(){
        return SignInDto.of(userId, password);
    }
}

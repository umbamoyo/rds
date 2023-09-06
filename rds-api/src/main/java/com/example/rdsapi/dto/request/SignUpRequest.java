package com.example.rdsapi.dto.request;

import com.example.rdsapi.dto.SignUpDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public record SignUpRequest(
        @Email @NotBlank(message = "이메일을 입력해주세요.")
        String userId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}", message = "비밀번호는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        String password,

        @NotBlank(message = "비밀번호 확인란은 필수입니다.")
        String checkPassword,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,12}$", message = "닉네임은 특수문자를 제외한 2~12자리여야 합니다.")
        String nickName
) {
    public SignUpDto toDto(){
        return SignUpDto.of(userId, password, checkPassword, nickName);
    }

}

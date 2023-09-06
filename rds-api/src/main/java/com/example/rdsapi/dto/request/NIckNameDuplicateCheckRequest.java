package com.example.rdsapi.dto.request;

import com.example.rdsapi.dto.NickNameDuplicateDto;
import com.example.rdsapi.dto.UserIdDuplicateDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public record NIckNameDuplicateCheckRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,12}$", message = "닉네임은 특수문자를 제외한 2~12자리여야 합니다.")
        String nickName
) {
    public NickNameDuplicateDto toDto(){
        return NickNameDuplicateDto.of(nickName);
    }

}

package com.example.rdsapi.dto.request;

import com.example.rdsapi.dto.SignUpDto;
import com.example.rdsapi.dto.UserIdDuplicateDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public record UserIdDuplicateCheckRequest(
        @Email @NotBlank(message = "이메일을 입력해주세요.")
        String userId
) {
    public UserIdDuplicateDto toDto(){
        return UserIdDuplicateDto.of(userId);
    }

}

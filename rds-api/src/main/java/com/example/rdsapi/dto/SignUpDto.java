package com.example.rdsapi.dto;

import com.example.rdscommon.domain.UserAccount;

public record SignUpDto(
    String userId,
    String password,
    String checkPassword,
    String nickName
){
        public static SignUpDto of(String userId, String password, String checkPassword, String nickName){
            return new SignUpDto(userId, password, checkPassword, nickName);
        }

        public UserAccount toEntity(){
            return UserAccount.of(userId, password,nickName,"");
        }
}

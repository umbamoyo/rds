package com.example.rdsapi.dto;

public record NickNameDuplicateDto(
        String nickName
){
    public static NickNameDuplicateDto of(String nickName){
        return new NickNameDuplicateDto(nickName);
    }

}

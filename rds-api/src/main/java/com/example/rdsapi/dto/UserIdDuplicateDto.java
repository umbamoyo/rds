package com.example.rdsapi.dto;

import com.example.rdscommon.domain.UserAccount;

public record UserIdDuplicateDto (
        String userId
){
    public static UserIdDuplicateDto of(String userId){
        return new UserIdDuplicateDto(userId);
    }

}

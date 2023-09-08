package com.example.rdsapi.domain;

import com.example.rdscommon.domain.RoleType;
import com.example.rdscommon.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;

public record UserAccountDto(
        String userId,
        String userPassword,
        String nickname,
        String memo,
        Set<RoleType> roleTypes,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static UserAccountDto of(String userId,
                                    String userPassword,
                                    String nickname,
                                    String memo,
                                    Set<RoleType> roleTypes)
    {
        return new UserAccountDto(userId, userPassword, nickname, memo, roleTypes, null, null, null, null);
    }
    public static UserAccountDto of(
            String userId,
            String userPassword,
            String nickname,
            String memo,
            Set<RoleType> roleTypes,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy)
    {
        return new UserAccountDto(userId, userPassword, nickname, memo, roleTypes, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDto from(UserAccount entity){
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getRoleTypes(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public UserAccount toEntity(){
        return UserAccount.of(
                userId,
                userPassword,
                nickname,
                memo
        );
    }
}

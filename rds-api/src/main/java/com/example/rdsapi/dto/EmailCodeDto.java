package com.example.rdsapi.dto;

import com.example.rdscommon.domain.EmailCode;

public record EmailCodeDto(
        String userId,
        String code
) {
    public static EmailCodeDto of(String userId) { return EmailCodeDto.of(userId, null);
    }
    public static EmailCodeDto of(String userId, String code) { return new EmailCodeDto(userId, code); }

    public EmailCode toEntity() {
        return EmailCode.of(userId, code);
    }
}

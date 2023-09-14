package com.example.rdsapi.dto.response;

import com.example.rdsapi.security.domain.TokenBox;

public record UpdateTokenResponse(
        TokenBox tokenBox
) {
    public static UpdateTokenResponse from(TokenBox tokenBox){
        return new UpdateTokenResponse(tokenBox);
    }
}

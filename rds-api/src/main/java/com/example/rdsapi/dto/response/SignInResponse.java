package com.example.rdsapi.dto.response;

import com.example.rdsapi.security.domain.TokenBox;

import java.util.List;

public record SignInResponse(
        TokenBox tokenBox,
        String nickName
) {
    public static SignInResponse from(List<String> tokenWithNickName){
        TokenBox result = new TokenBox();
        result.setAccessToken(tokenWithNickName.get(0));
        result.setRefreshToken(tokenWithNickName.get(1));

        return new SignInResponse(result, tokenWithNickName.get(2));
    }
}

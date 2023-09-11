package com.example.rdsapi.dto;

public record SignInDto(
        String userId,
        String password
) {
    public static SignInDto of(String userId, String password){
        return new SignInDto(userId, password);
    }
}

package com.example.rdsapi.service;


import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import com.example.rdsapi.security.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtUtil jwtUtil;

    public RefreshToken createRefreshToken(UserPrincipal user) {

        String jti = UUID.randomUUID().toString();
        String token = jwtUtil.generateRefreshToken(user, jti);

        //TODO: RefreshToken 을 DB로 관리할 경우를 대비한 코드
        //TODO: RefreshToken 상세 스펙은 변경될 수 있음
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(token);
        refreshToken.setExpiryDate(jwtUtil.getTokenExpiryFromJWT(token).toInstant());
        refreshToken.setRevoked(false);
        refreshToken.setJti(jti);

        return refreshToken;
    }


}

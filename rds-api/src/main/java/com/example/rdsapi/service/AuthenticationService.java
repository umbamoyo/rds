package com.example.rdsapi.service;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.SignInDto;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.security.domain.TokenBox;
import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 로그인
    public List<String> authenticate(SignInDto dto) {
        Authentication auth = authenticate(dto.userId(), dto.password());

        final UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal, UUID.randomUUID().toString(), null);
        String accessToken  = jwtUtil.generateAccessToken(userPrincipal);

        return List.of(accessToken, refreshToken, userPrincipal.nickname());
    }

    // SpringSecurity 를 이용하여 인증단계를 구현(로그인)
    private Authentication authenticate(String userId, String password){

        try{
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, password));
        }catch (DisabledException e){
            throw new GeneralException(ErrorCode.USER_INACTIVE);
        }catch (BadCredentialsException e){
            throw new GeneralException(ErrorCode.NOT_INVALID_ID_OR_PASSWORD);
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException(ErrorCode.NOT_INVALID_ID_OR_PASSWORD);
        }
    }

    // refreshToken을 이용한 token-pair 갱신
    public TokenBox generateNewTokenPair(String accessToken, String refreshToken, String ip, String os, String browser){
        Claims parsedRefreshToken = null;
        Claims parsedAccessToken = null;

        // refreshToken의 유효기간이 만료 검사
        try{
            parsedRefreshToken = jwtUtil.parseToken(refreshToken);
        }catch (GeneralException e){
            if(e.getErrorCode() == ErrorCode.TOKEN_EXPIRED){
                throw new GeneralException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
        }

        // 유효기간이 만료된 accessToken 파싱
        parsedAccessToken = jwtUtil.parseExpiredToken(accessToken);


        // refreshToken 과 accessToken의 사용자 정보가 일치하는지 확인
        if(
                !jwtUtil.getUserIdFromJWT(parsedAccessToken).equals(jwtUtil.getUserIdFromJWT(parsedRefreshToken)) ||
                !jwtUtil.getNickNameFromToken(parsedAccessToken).equals(jwtUtil.getNickNameFromToken(parsedRefreshToken)) ||
                !jwtUtil.getTokenTypeFromJWT(parsedAccessToken).equals(JwtUtil.TokenType.ACCESS_TOKEN.toString()) ||
                !jwtUtil.getTokenTypeFromJWT(parsedRefreshToken).equals(JwtUtil.TokenType.REFRESH_TOKEN.toString())
        ){
            throw new GeneralException(ErrorCode.NOT_MATCH_TOKEN_PAIR);
        }


        // 토큰 쌍 갱신
        UserPrincipal userPrincipal = UserPrincipal.of(
                jwtUtil.getUserIdFromJWT(parsedAccessToken),
                null,
                jwtUtil.getNickNameFromToken(parsedAccessToken),
                null,
                jwtUtil.getAuthoritiesFromJWT(parsedAccessToken)
        );

        String newAccessToken = jwtUtil.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtUtil.generateRefreshToken(userPrincipal, UUID.randomUUID().toString(), jwtUtil.getTokenExpiryFromJWT(parsedRefreshToken));

        return new TokenBox(newAccessToken, newRefreshToken);
    }

    public String getClientOS(String userAgent) {
        String os = "";
        userAgent = userAgent.toLowerCase();
        
        if(userAgent.contains("windows nt 11.0")){
            os = "Windows11";
        }else if (userAgent.contains("windows nt 10.0")) {
            os = "Windows10";
        }else if (userAgent.contains("windows nt 6.1")) {
            os = "Windows7";
        }else if (userAgent.contains("windows nt 6.2")  || userAgent.contains("windows nt 6.3")  ) {
            os = "Windows8";
        }else if (userAgent.contains("windows nt 6.0")) {
            os = "WindowsVista";
        }else if (userAgent.contains("windows nt 5.1")) {
            os = "WindowsXP";
        }else if (userAgent.contains("windows nt 5.0")) {
            os = "Windows2000";
        }else if (userAgent.contains("windows nt 4.0")) {
            os = "WindowsNT";
        }else if (userAgent.contains("windows 98")) {
            os = "Windows98";
        }else if (userAgent.contains("windows 95")) {
            os = "Windows95";
        }else if (userAgent.contains("iphone")) {
            os = "iPhone";
        }else if (userAgent.contains("ipad")) {
            os = "iPad";
        }else if (userAgent.contains("android")) {
            os = "android";
        }else if (userAgent.contains("mac")) {
            os = "mac";
        }else if (userAgent.contains("linux")) {
            os = "Linux";
        }else{
            os = "Other";
        }
        return os;
    }

    public String getClientBrowser(String userAgent) {
        String browser = "";

        if (userAgent.contains("Trident/7.0")) {
            browser = "ie11";
        }
        else if (userAgent.contains("MSIE 10")) {
            browser = "ie10";
        }
        else if (userAgent.contains("MSIE 9")) {
            browser = "ie9";
        }
        else if (userAgent.contains("MSIE 8")) {
            browser = "ie8";
        }
        else if (userAgent.contains("Chrome/")) {
            browser = "Chrome";
        }
        else if (userAgent.contains("Chrome/") && userAgent.contains("Safari/")) {
            browser = "Safari";
        }
        else if (userAgent.contains("Firefox/")) {
            browser = "Firefox";
        }
        else {
            browser ="Other";
        }
        return browser;
    }


}
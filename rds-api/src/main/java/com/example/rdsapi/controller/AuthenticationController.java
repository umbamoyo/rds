package com.example.rdsapi.controller;

import com.example.rdsapi.dto.request.CheckAuthenticationEmailCodeRequest;
import com.example.rdsapi.dto.request.SendAuthenticationEmailCodeRequest;
import com.example.rdsapi.dto.request.SignInRequest;
import com.example.rdsapi.dto.response.SignInResponse;
import com.example.rdsapi.dto.response.common.ApiDataResponse;
import com.example.rdsapi.security.domain.TokenBox;
import com.example.rdsapi.service.AuthenticationService;
import com.example.rdsapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
/**
 * 로그인, 토큰 갱신, 이메일 인증 등 인증과 관련된 작업을 진행
 */
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @PostMapping("/signIn")
    public ApiDataResponse<SignInResponse> login(
            @RequestBody SignInRequest signInRequest,
            HttpServletRequest request
    ){
        List<String> tokenWithNickName = authenticationService.authenticate(signInRequest.toDto(), getUserEnvInfo(request));

        return ApiDataResponse.of(SignInResponse.from(tokenWithNickName));

    }

    @PostMapping("/sendAuthenticationEmailCode")
    public ApiDataResponse<Object> sendAuthenticationEmailCode(
            @Valid @RequestBody SendAuthenticationEmailCodeRequest request
    ){
        emailService.sendSignUpAuthenticationMail(request.toDto());
        return ApiDataResponse.emptyWithCustomMessage("이메일을 수신하지 못하였으면 재 전송 버튼을 눌러주세요");
    }

    @PostMapping("/checkAuthenticationEmailCode")
    public ApiDataResponse<Object> checkAuthenticationEmailCode(
            @Valid @RequestBody CheckAuthenticationEmailCodeRequest request
    ){
        emailService.authenticate(request.toDto());
        return ApiDataResponse.emptyWithCustomMessage("Success Email Authentication");
    }

    @GetMapping("/updateToken")
    public ApiDataResponse<TokenBox> updateToken(
            @RequestHeader("ACCESS_TOKEN") String accessToken,
            @RequestHeader("REFRESH_TOKEN") String refreshToken,
            HttpServletRequest request
    ){
        // client 접속 정보 가져오기
        Map<String, String> clientInfo = getUserEnvInfo(request);

        // 토큰 갱신
        TokenBox tokenBox = authenticationService.generateNewTokenPair(accessToken.substring("Bearer ".length()), refreshToken.substring("Bearer ".length()), clientInfo);
        return ApiDataResponse.of(tokenBox);
    }

    // 사용자의 ip, os, browser 정보를 가져옴
    private Map<String, String> getUserEnvInfo(HttpServletRequest request){
        String userAgent = request.getHeader("USER-AGENT");
        String os = getClientOS(userAgent);
        String browser = getClientBrowser(userAgent);
        String ip = getClientIp(request);

        Map<String, String> info = new HashMap<>();
        info.put("os", os);
        info.put("browser", browser);
        info.put("ip", ip);

        return info;
    }

    private String getClientIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null)
            ip = request.getRemoteAddr();
        return ip;
    }

    private String getClientOS(String userAgent) {
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

    private String getClientBrowser(String userAgent) {
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

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
import java.util.List;

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
            @RequestBody SignInRequest request
            ){
        List<String> tokenWithNickName = authenticationService.authenticate(request.toDto());

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
        // 사용자 ip 가져오기
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();

        // os 정보 가져오기
        String userAgent = request.getHeader("USER-AGENT");
        String os = authenticationService.getClientOS(userAgent);
        String browser = authenticationService.getClientBrowser(userAgent);

        TokenBox tokenBox = authenticationService.generateNewTokenPair(accessToken.substring("Bearer ".length()), refreshToken.substring("Bearer ".length()), ip, os, browser);
        return ApiDataResponse.of(tokenBox);
    }

}

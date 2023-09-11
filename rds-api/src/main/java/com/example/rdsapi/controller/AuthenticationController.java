package com.example.rdsapi.controller;

import com.example.rdsapi.dto.request.SendAuthenticationEmailCodeRequest;
import com.example.rdsapi.dto.request.SignInRequest;
import com.example.rdsapi.dto.response.SignInResponse;
import com.example.rdsapi.dto.response.common.ApiDataResponse;
import com.example.rdsapi.service.AuthenticationService;
import com.example.rdsapi.service.EmailService;
import com.example.rdsapi.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @PostMapping("/signIn")
    public ApiDataResponse<SignInResponse> login(
            @RequestBody SignInRequest request
            ){
        List<String> tokenWithNickName = authenticationService.authenticate(request.toDto());

        return ApiDataResponse.of(SignInResponse.from(tokenWithNickName));

    }

//    @PostMapping("/refresh-token")
//    public ApiDataResponse<RefreshToken.RefreshTokenRes> refreshJwtToken(
//            @RequestBody RefreshToken.RefreshTokenReq req
//    ){
//        RefreshToken.RefreshTokenRes res = refreshTokenService.refreshAccessToken(
//                RefreshToken.RefreshTokenDto.fromRefreshTokenReq(req)
//        );
//
//        return ApiDataResponse.of(res);
//    }


    @PostMapping("/sendAuthenticationEmailCode")
    public ApiDataResponse<Object> sendAuthenticationEmailCode(
            @Valid @RequestBody SendAuthenticationEmailCodeRequest request
    ) throws Exception {
        String code = emailService.sendSimpleMessage(request.userId());

    }

}

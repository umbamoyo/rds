package com.example.rdsapi.controller;

import com.example.rdsapi.dto.request.NIckNameDuplicateCheckRequest;
import com.example.rdsapi.dto.request.SignUpRequest;
import com.example.rdsapi.dto.request.UserIdDuplicateCheckRequest;
import com.example.rdsapi.dto.response.common.ApiDataResponse;
import com.example.rdsapi.service.EncryptService;
import com.example.rdsapi.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserAccountService userAccountService;
    private final EncryptService encryptService;


    @PostMapping("/signUp")
    public ApiDataResponse<Object> signUp(@Valid @RequestBody SignUpRequest request){
        encryptService.signUp(request.toDto());
        return ApiDataResponse.empty();
    }

    @PostMapping("/nickNameDuplicateCheck")
    public ApiDataResponse<Object> nickNameDuplicateCheck(@Valid @RequestBody NIckNameDuplicateCheckRequest request){
        userAccountService.nickNameDuplicateCheck(request.toDto().nickName());
        return ApiDataResponse.emptyWithCustomMessage("사용 가능한 닉네임 입니다.");
    }

    @PostMapping("/userIdDuplicateCheck")
    public ApiDataResponse<Object> userIdDuplicateCheck(@Valid @RequestBody UserIdDuplicateCheckRequest request){
        userAccountService.userIdDuplicateCheck(request.toDto().userId());
        return ApiDataResponse.emptyWithCustomMessage("사용 가능한 이메일 입니다.");
    }

    @GetMapping("/test")
    public ApiDataResponse<Object> test(){
        return ApiDataResponse.empty();
    }
}

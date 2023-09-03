package com.example.rdsapi.controller;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.response.common.ApiDataResponse;
import com.example.rdsapi.service.DemoService;
import com.example.rdscommon.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/save")
    public String save(){
        System.out.println(ErrorCode.OK.getCode());
        return demoService.save();
    }

    @GetMapping("/find")
    public String find(){
        return demoService.find();
    }


    @GetMapping("/findAllUser")
    public ApiDataResponse<List<UserAccount>> findAllUser(){
        return ApiDataResponse.of(demoService.findAll());
    }
}

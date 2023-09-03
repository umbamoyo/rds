package com.example.rdsapi.controller;

import com.example.rdsapi.service.DemoService;
import com.example.rdscommon.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/save")
    public String save(){
        System.out.println(ErrorCode.SUCCESS.getCode());
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

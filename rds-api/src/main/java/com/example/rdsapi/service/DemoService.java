package com.example.rdsapi.service;

import com.example.rdscommon.service.CommonDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final CommonDemoService commonDemoService;

    public String save(){
        System.out.println(commonDemoService.commonService());
        return "save";
    }

    public String find(){
        return "find";
    }
}

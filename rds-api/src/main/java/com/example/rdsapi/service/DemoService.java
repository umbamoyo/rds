package com.example.rdsapi.service;

import com.example.rdscommon.domain.UserAccount;
import com.example.rdscommon.repository.UserAccountRepository;
import com.example.rdscommon.service.CommonDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final CommonDemoService commonDemoService;
    private final UserAccountRepository userAccountRepository;

    public String save(){
        System.out.println(commonDemoService.commonService());
        return "save";
    }

    public String find(){
        return "find";
    }

    public List<UserAccount> findAll() {
        userAccountRepository.save(UserAccount.of("jyuka","111","111","111","111"));
        return userAccountRepository.findAll();
    }
}

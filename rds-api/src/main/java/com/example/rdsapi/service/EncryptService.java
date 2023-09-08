package com.example.rdsapi.service;

import com.example.rdsapi.dto.SignUpDto;
import com.example.rdscommon.domain.RoleType;
import com.example.rdscommon.domain.UserAccount;
import com.example.rdscommon.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EncryptService {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountService userAccountService;



    // 회원가입 로직
    public void signUp(SignUpDto dto){

        // 중복 아이디 체크
        userAccountService.userIdDuplicateCheck(dto.userId());

        // 중복 닉네임 체크
        userAccountService.nickNameDuplicateCheck(dto.nickName());

        // 비밀번호, 비밀번호 확인 일치 여부 확인
        userAccountService.passwordDuplicateCheck(dto.password(), dto.checkPassword());
        UserAccount userAccount  = dto.toEntity();
        userAccount.setRoleTypes(Set.of(new RoleType("ROLE_USER")));
        userAccount.setUserPassword(passwordEncoder.encode(dto.password()));
        userAccountRepository.save(userAccount);

    }
}

package com.example.rdsapi.service;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdscommon.domain.UserAccount;
import com.example.rdscommon.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    // 비밀번호, 비밀번화 확인 일치 여부 확인
    public boolean passwordDuplicateCheck(String password, String rePassword){
        if(!password.equals(rePassword)){
            throw new GeneralException(ErrorCode.VALIDATION_ERROR, "패스워드가 일치하지 않습니다.");
        }
        return true;
    }

    // 중복 닉네임 여부 체크
    public boolean nickNameDuplicateCheck(String nickName){
        if(userAccountRepository.existsByNickname(nickName)){
            throw new GeneralException(ErrorCode.NICKNAME_EXIST, "이미 사용중인 닉네임 입니다.");
        }
        return true;
    }

    // 중복 아이디 여부 체크
    public boolean userIdDuplicateCheck(String userId){
        if(userAccountRepository.existsById(userId)){
            throw new GeneralException(ErrorCode.ID_EXIST, "이미 사용중인 이메일 입니다.");
        }
        return true;
    }

    public UserPrincipal getUserAccountById(String userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_INVALID_ID_OR_PASSWORD));

        return UserPrincipal.of(
                userAccount.getUserId(),
                userAccount.getUserPassword(),
                userAccount.getNickname(),
                userAccount.getMemo(),
                userAccount.getRoleTypes()
        );
    }

}

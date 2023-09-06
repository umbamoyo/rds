package com.example.rdsapi.service;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.SignUpDto;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdscommon.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    // 회원가입 로직
    public void signUp(SignUpDto dto){

        // 중복 아이디 체크
        userIdDuplicateCheck(dto.userId());

        // 중복 닉네임 체크
        nickNameDuplicateCheck(dto.nickName());

        // 비밀번호, 비밀번호 확인 일치 여부 확인
        passwordDuplicateCheck(dto.password(), dto.checkPassword());

        userAccountRepository.save(dto.toEntity());

    }


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
}

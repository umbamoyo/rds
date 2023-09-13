package com.example.rdsapi.security.filter;


import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import com.example.rdscommon.repository.UserAccountRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;
    private final UserAccountRepository userAccountRepository;


    // 해당 메서드를 통해 jwt 토큰 값을 유저 아이디와 권한을 매핑한 형태의 토큰을 반환한다.
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        String jwt = (String) auth.getPrincipal();

        JwtAuthenticationToken jwtAuthenticationToken = null;

        String tokenType = null;
        Claims parsedToken = null;



        try{
            parsedToken = jwtUtil.parseToken(jwt);
            tokenType = jwtUtil.getTokenTypeFromJWT(parsedToken);
        }catch (GeneralException e){
            // 토큰의 유효기간이 지났다는 에러코드인 경우
            if(e.getErrorCode().getCode() == 3004){
                throw new GeneralException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            }
        }


        String userId = jwtUtil.getUserIdFromJWT(parsedToken);
        if(!userAccountRepository.existsById(userId)){
            throw new GeneralException(ErrorCode.USER_NOT_FOUND);
        }

        List<GrantedAuthority> authorities = null;
        if(tokenType.equals(JwtUtil.TokenType.ACCESS_TOKEN.toString())){
            authorities = jwtUtil.getAuthoritiesFromJWT(parsedToken);
            authorities.forEach(i-> System.out.println(i.getAuthority()));
        }

        UserPrincipal principal =  UserPrincipal.of(userId, authorities);

        jwtAuthenticationToken = new JwtAuthenticationToken(principal, authorities);
        jwtAuthenticationToken.setAuthenticated(true);

        return jwtAuthenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }



}
package com.example.rdsapi.security.filter;


import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;


    // 해당 메서드를 통해 jwt 토큰 값을 유저 아이디와 권한을 매핑한 형태의 토큰을 반환한다.
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        String jwt = (String) auth.getPrincipal();

        JwtAuthenticationToken jwtAuthenticationToken = null;


        String tokenType = null;
        boolean tokenValid = false;


        tokenType = jwtUtil.getTokenTypeFromJWT(jwt);
        tokenValid = jwtUtil.validateToken(jwt);


        String userId = jwtUtil.getUsernameFromJWT(jwt);
        if(!userAccountRepository.existsById(userId)){
            throw new GeneralException(ErrorCode.USER_NOT_FOUND);
        }

        List<GrantedAuthority> authorities = null;
        if(tokenType.equals(JwtUtil.TokenType.ACCESS_TOKEN.toString())){
            authorities = jwtUtil.getAuthoritiesFromJWT(jwt);
            authorities.forEach(i-> System.out.println(i.getAuthority()));
        }

        UserPrincipal principal =  UserPrincipal.of(userId, authorities);

        jwtAuthenticationToken = new JwtAuthenticationToken(principal, authorities);
        jwtAuthenticationToken.setAuthenticated(tokenValid);

        return jwtAuthenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }



}
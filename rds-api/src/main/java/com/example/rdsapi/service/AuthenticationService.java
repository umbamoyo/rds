package com.example.rdsapi.service;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.SignInDto;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.security.domain.RefreshToken;
import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;



    public List<String> authenticate(SignInDto dto) {
        Authentication auth = authenticate(dto.userId(), dto.password());

        final UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal);
        String accessToken  = jwtUtil.generateAccessToken(userPrincipal);

        return List.of(accessToken, refreshToken.getRefreshToken(), userPrincipal.nickname());
    }

    // SpringSecurity 를 이용하여 인증단계를 구현
    private Authentication authenticate(String userId, String password){

        try{
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, password));
        }catch (DisabledException e){
            throw new GeneralException(ErrorCode.USER_INACTIVE);
        }catch (BadCredentialsException e){
            throw new GeneralException(ErrorCode.NOT_INVALID_ID_OR_PASSWORD);
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException(ErrorCode.NOT_INVALID_ID_OR_PASSWORD);
        }
    }
}
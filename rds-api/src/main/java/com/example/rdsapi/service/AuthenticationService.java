package com.example.rdsapi.service;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.SignInDto;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.security.domain.TokenBox;
import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import com.example.rdscommon.domain.RefreshToken;
import com.example.rdscommon.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    // 로그인
    @Transactional
    public List<String> authenticate(SignInDto dto, Map<String, String> clientInfo) {
        Authentication auth = authenticate(dto.userId(), dto.password());

        final UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        String jtl = makeJti();

        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal, jtl, null);
        String accessToken  = jwtUtil.generateAccessToken(userPrincipal);

        refreshTokenRepository.deleteByUserIdAndIpAndOsAndBrowser(
                userPrincipal.userId(),
                clientInfo.get("ip"),
                clientInfo.get("os"),
                clientInfo.get("browser")
        );

        // Refresh 토큰 생성 정보를 DB에 저장
        refreshTokenRepository.save(
            RefreshToken.of(
                    userPrincipal.userId(),
                    refreshToken,
                    jtl,
                    clientInfo.get("ip"),
                    clientInfo.get("os"),
                    clientInfo.get("browser")
            )
        );

        return List.of(accessToken, refreshToken, userPrincipal.nickname());
    }

    // SpringSecurity 를 이용하여 인증단계를 구현(로그인)
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

    // refreshToken을 이용한 token-pair 갱신
    @Transactional
    public TokenBox generateNewTokenPair(String accessToken, String refreshToken, Map<String, String> clientInfo){
        Claims parsedRefreshToken = null;
        Claims parsedAccessToken = null;
        RefreshToken selectRefreshTokenFromDB = null;

        try{
            // refreshToken의 유효기간이 만료 검사
            parsedRefreshToken = jwtUtil.parseToken(refreshToken);

            // DB를 조회하여 유효한 사용자인지 판별
            String beforeJti = parsedRefreshToken.getId();
            selectRefreshTokenFromDB = refreshTokenRepository.findByJti(beforeJti)
                    .orElseThrow(() -> new GeneralException(ErrorCode.ACCESS_DENIED));

            if(
                    !selectRefreshTokenFromDB.getRefreshToken().equals(refreshToken) ||
                    !selectRefreshTokenFromDB.getIp().equals(clientInfo.get("ip")) ||
                    !selectRefreshTokenFromDB.getBrowser().equals(clientInfo.get("browser")) ||
                    !selectRefreshTokenFromDB.getOs().equals(clientInfo.get("os"))
            ){
                throw new GeneralException(ErrorCode.ACCESS_DENIED);
            }
        }catch (GeneralException e){
            if(e.getErrorCode() == ErrorCode.TOKEN_EXPIRED){
                throw new GeneralException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
        }


        // 유효기간이 만료된 accessToken 파싱
        parsedAccessToken = jwtUtil.parseExpiredToken(accessToken);


        // refreshToken 과 accessToken의 사용자 정보가 일치하는지 확인
        if(
                !jwtUtil.getUserIdFromJWT(parsedAccessToken).equals(jwtUtil.getUserIdFromJWT(parsedRefreshToken)) ||
                !jwtUtil.getNickNameFromToken(parsedAccessToken).equals(jwtUtil.getNickNameFromToken(parsedRefreshToken)) ||
                !jwtUtil.getTokenTypeFromJWT(parsedAccessToken).equals(JwtUtil.TokenType.ACCESS_TOKEN.toString()) ||
                !jwtUtil.getTokenTypeFromJWT(parsedRefreshToken).equals(JwtUtil.TokenType.REFRESH_TOKEN.toString())
        ){
            throw new GeneralException(ErrorCode.NOT_MATCH_TOKEN_PAIR);
        }


        // 토큰 쌍 갱신
        UserPrincipal userPrincipal = UserPrincipal.of(
                jwtUtil.getUserIdFromJWT(parsedAccessToken),
                null,
                jwtUtil.getNickNameFromToken(parsedAccessToken),
                null,
                jwtUtil.getAuthoritiesFromJWT(parsedAccessToken)
        );

        String afterJti = makeJti();
        String newAccessToken = jwtUtil.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtUtil.generateRefreshToken(userPrincipal, afterJti , jwtUtil.getTokenExpiryFromJWT(parsedRefreshToken));

        // DB에 갱신된 RefreshToken 정보 수정
        selectRefreshTokenFromDB.setJti(afterJti);
        selectRefreshTokenFromDB.setRefreshToken(newRefreshToken);
        refreshTokenRepository.save(selectRefreshTokenFromDB);

        return new TokenBox(newAccessToken, newRefreshToken);
    }


    private String makeJti(){
        String jtl = UUID.randomUUID().toString();
        while (refreshTokenRepository.existsByJti(jtl)){
            jtl = UUID.randomUUID().toString();
        }
        return jtl;
    }


}
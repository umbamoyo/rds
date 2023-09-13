package com.example.rdsapi.util;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.security.domain.UserPrincipal;
import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String NICKNAME_CLAIM = "nickname";
    private static final String SECRET_KEY = "whoKnowsMyKey?blahblah";

    // prod : access(6시간), refresh(24주)
//    public static long ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(60*6).toMillis();
//    public static long REFRESH_TOKEN_EXPIRATION_TIME = 604800000L;

    // dev : access(1분), refresh(3분)
    public static long ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(1).toMillis();
    public static long REFRESH_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(3).toMillis();


    public String generateAccessToken(UserPrincipal userPrincipal) {
        Instant expiryDate = Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION_TIME);
        String authorities = getUserAuthorities(userPrincipal.authorities());
        return Jwts.builder()
                .setSubject(userPrincipal.userId())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiryDate))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .claim(AUTHORITIES_CLAIM, authorities)
                .claim(TOKEN_TYPE_CLAIM, TokenType.ACCESS_TOKEN.toString())
                .claim(NICKNAME_CLAIM, userPrincipal.nickname())
                .compact();


    }

    private String getUserAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public String generateRefreshToken(UserPrincipal userPrincipal, String jti, Date haveExpiredDate) {
        Date expireDate = haveExpiredDate == null ? Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME)) : haveExpiredDate;
        return Jwts.builder()
                .setSubject(userPrincipal.userId())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(expireDate)
                .setId(jti)
                .claim(TOKEN_TYPE_CLAIM, TokenType.REFRESH_TOKEN.toString())
                .claim(NICKNAME_CLAIM, userPrincipal.nickname())
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 토큰 만료 일자를 반환
    public Date getTokenExpiryFromJWT(Claims parsedToken) {
        return parsedToken.getExpiration();
    }

    // JWT 파싱
    public Claims parseToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException  | UnsupportedJwtException | SignatureException ex) {
            throw new GeneralException(ErrorCode.IS_NOT_JWT);
        } catch (Exception e){
            throw new GeneralException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // 만료된 accessToken이 유효한 값을 가지면서 재갱신 요쳥을 시도하는지 판단하기 위함
    public Claims parseExpiredToken(String token){
        try{
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }catch (MalformedJwtException  | UnsupportedJwtException | SignatureException ex) {
            ex.printStackTrace();
            throw new GeneralException(ErrorCode.IS_NOT_JWT);
        } catch (Exception e){
            e.printStackTrace();
            throw new GeneralException(ErrorCode.INTERNAL_ERROR);
        }
    }


    // 토큰 타입을 반환 : accessToken or refreshToken
    public String getTokenTypeFromJWT(Claims parsedToken) {
        return (String) parsedToken.get(TOKEN_TYPE_CLAIM);
    }

    // 사용자 닉네임을 반환
    public String getNickNameFromToken(Claims parsedToken){
        return (String) parsedToken.get(NICKNAME_CLAIM);
    }

    // 토큰에서 유저 아이디를 반환
    public String getUserIdFromJWT(Claims parsedToken) {
        return parsedToken.getSubject();
    }


    // 사용자 권한들을 반환
    public List<GrantedAuthority> getAuthoritiesFromJWT(Claims parsedToken) {
        return Arrays.stream(parsedToken.get(AUTHORITIES_CLAIM).toString().split(","))
                .map( role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }


    public enum TokenType {

        REFRESH_TOKEN("refresh_token"),
        ACCESS_TOKEN("access_token");

        private final String tokenType;

        private TokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public static TokenType fromString(String roleName) throws IllegalArgumentException {
            return Arrays.stream(TokenType.values())
                    .filter(x -> x.tokenType.equals(roleName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + roleName));
        }

        @Override
        public String toString() {
            return tokenType;
        }

    }
}

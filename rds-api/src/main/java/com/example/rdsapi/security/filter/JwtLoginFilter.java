package com.example.rdsapi.security.filter;


import com.example.rdsapi.dto.request.SignInRequest;
import com.example.rdsapi.security.domain.UserPrincipal;
import com.example.rdsapi.util.JwtUtil;
import com.example.rdsapi.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserAccountService userAccountService;
    private final JwtUtil jwtUtil;

    public JwtLoginFilter(AuthenticationManager authenticationManager, UserAccountService userAccountService, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.userAccountService = userAccountService;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/signIn");
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException
    {
        String refreshToken = request.getHeader("REFRESH_TOKEN");

        // Refresh Token 이 존재하지 않아 로그인을 다시 해야 하는 경우
        if(refreshToken == null) {
            SignInRequest signInRequest = objectMapper.readValue(request.getInputStream(), SignInRequest.class);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    signInRequest.userId(), signInRequest.password(), null
            );

            return getAuthenticationManager().authenticate(token);
        }else{
            jwtUtil.validateToken(refreshToken);
            UserPrincipal principal = userAccountService.getUserAccountById(jwtUtil.getUsernameFromJWT(refreshToken));
            return new UsernamePasswordAuthenticationToken(
                    principal, principal.getAuthorities()
            );
        }
    }

    /**
     * 인증과정이 완료되면 jwt 토큰을 생성한다.
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException
    {
        UserPrincipal user = (UserPrincipal) authResult.getPrincipal();
        response.setHeader("auth_token", jwtUtil.generateAccessToken(user));
        response.setHeader("refresh_token", jwtUtil.generateRefreshToken(user,"sample"));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(user));
    }
}
package com.example.rdsapi.security.filter;


import com.example.rdsapi.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtCheckFilter extends BasicAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtCheckFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String bearer = request.getHeader("ACCESS_TOKEN");

        // 토큰이 존재하지 않거나 토큰 갱신을 요청하는 경우
        if (bearer == null || !bearer.startsWith("Bearer ") || request.getRequestURI().contains("/api/v1/updateToken")) {
            chain.doFilter(request, response);
            return;
        }
        String token = bearer.substring("Bearer ".length());


        // 파싱한 jwt 토큰을 통해 SpringSecurity 에서 사용되는 인증 토큰 형태로 변환
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token);

        // JwtAuthenticationToken 은 JwtAuthenticationProvider 를 통해 유효성 검사를 진행
        Authentication authentication = getAuthenticationManager().authenticate(jwtAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
        return;
    }

}


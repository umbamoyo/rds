package com.example.rdsapi.security.filter;


import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.response.common.ApiErrorResponse;
import com.example.rdsapi.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtCheckExceptionHandler extends BasicAuthenticationFilter {

    ObjectMapper objectMapper;

    public JwtCheckExceptionHandler(AuthenticationManager authenticationManager,
                                    ObjectMapper objectMapper) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            chain.doFilter(request,response);
        }catch (GeneralException e){
            setErrorResponse(e.getErrorCode(), response);
        }
    }

    public void setErrorResponse(ErrorCode errorCode, HttpServletResponse response){
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(false, errorCode);

        try{
            String json = objectMapper.writeValueAsString(apiErrorResponse);
            response.getWriter().write(json);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

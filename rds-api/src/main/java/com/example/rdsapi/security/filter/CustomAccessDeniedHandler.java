package com.example.rdsapi.security.filter;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.dto.response.common.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SpringSecurity Filter chain 을 활용하여 AccessDenied 를 반환하는 경우 별도의 에러코드를 전달
 */
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
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
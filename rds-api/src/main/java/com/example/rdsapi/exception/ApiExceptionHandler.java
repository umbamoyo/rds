package com.example.rdsapi.exception;

import com.example.rdsapi.constant.ErrorCode;
import com.example.rdsapi.exception.GeneralException;
import com.example.rdsapi.exception.ValidError;
import com.example.rdsapi.dto.response.common.ApiErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring 에서 이미 구현된 Exception Handler 를 상속 받아 간단하게 예외처리
 */
@RestControllerAdvice(annotations = {RestController.class})
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    // AOP(@Validated)로 동작하는 기능 수행중 받 은 값이 제약사항을 위반한 경우
    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(e, ErrorCode.VALIDATION_ERROR, request);
    }

    // DB 무결성 위반 핸들링 : 중복된 닉네임, 이메일등을 입력할 경우 처리
    @ExceptionHandler
    public ResponseEntity<Object> validation(DataIntegrityViolationException e, WebRequest request) {
        return handleExceptionInternal(e, ErrorCode.DB_CONFLICT, request);
    }


    //request 값을 바인딩 하는 과정에 인자값에 문제가 생겨 오류가 발생하는 경우 어떤 파라미터가 문제가 있는지 출력
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError error = (FieldError) bindingResult.getAllErrors().stream().findFirst().get();
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        return super.handleExceptionInternal(
                e,
                ApiErrorResponse.of(false, errorCode.getCode(), errorCode.getMessage(error.getDefaultMessage())),
                headers,
                status,
                request
        );

    }

    // 의도한 오류(GeneralException) 를 처리
    @ExceptionHandler
    public ResponseEntity<Object> general(GeneralException e, WebRequest request) {
        return handleExceptionInternal(e, e.getErrorCode(), request);
    }

    // 예상치 못한 에러가 발생한 경우
    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        return handleExceptionInternal(e, ErrorCode.INTERNAL_ERROR, request);
    }

    // Spring 에서 이미 구현된 Exception Handler 를 상속 받아 간단하게 예외처리
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, ErrorCode.valueOf(status), headers, status, request);
    }

    // 위의 메서드를 통해 오버라이드 작업을 진행하는데 필요한 커스텀 작업을 별도 분리
    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorCode errorCode, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(
                e,
                ApiErrorResponse.of(false, errorCode.getCode(), errorCode.getMessage(e)),
                headers,
                status,
                request
        );
    }


    // 임의로 에러를 발생시킨 경우 코드값을 리턴
    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorCode errorCode, WebRequest request) {
        return handleExceptionInternal(e, errorCode, HttpHeaders.EMPTY, errorCode.getHttpStatus(), request);
    }



}


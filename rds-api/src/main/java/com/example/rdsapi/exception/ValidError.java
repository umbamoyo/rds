package com.example.rdsapi.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
/**
 * MethodArgumentNotValidException 발생 시 오류 값과 필드를 객체와 시키기 위함
 */
public class ValidError {

    private String field;
    private String message;
    private String invalidValue;

}

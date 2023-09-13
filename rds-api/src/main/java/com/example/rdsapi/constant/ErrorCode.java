package com.example.rdsapi.constant;

import com.example.rdsapi.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    OK(0, HttpStatus.OK, "Ok"),
    BAD_REQUEST(10000, HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR(10001, HttpStatus.BAD_REQUEST, "Validation error"),
    ID_EXIST(1002,HttpStatus.CONFLICT,"Duplicate ID"),
    NICKNAME_EXIST(1003,HttpStatus.CONFLICT,"Duplicate NickName"),
    DB_CONFLICT(1003,HttpStatus.CONFLICT,"DB_CONFLICT"),
    ACCESS_DENIED(1004,HttpStatus.FORBIDDEN,"Access Denied"),
    USER_NOT_FOUND(1005, HttpStatus.BAD_REQUEST,"사용자 정보를 찾을 수 없습니다."),


    // 회원가입 관련 에러 처리
    NOT_INVALID_ID_OR_PASSWORD(2000,HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해 주세요."),
    EMAIL_AUTHENTICATION_CODE_EXPIRE(2004,HttpStatus.BAD_REQUEST, "인증유효시간이 만료되었습니다."),
    EMAIL_AUTHENTICATION_NOT_INVALID(2005,HttpStatus.BAD_REQUEST, "인증코드를 확인해 주세요."),


    // 토큰 관련 에러 처리
    ACCESS_TOKEN_EXPIRED(3000, HttpStatus.UNAUTHORIZED, "엑세스 토큰의 유효기간이 만료되었습니다. 리프레시 토큰을 사용하여 갱신을 시도해주세요."),
    REFRESH_TOKEN_EXPIRED(3001, HttpStatus.UNAUTHORIZED, "로그인 유효시간이 만료되었습니다. 다시 로그인을 해주세요."),
    IS_NOT_JWT(3002, HttpStatus.UNAUTHORIZED, "사용자 정보를 확인할 수 없습니다."),
    TOKEN_EXPIRED(3004, HttpStatus.UNAUTHORIZED, "토큰의 유효기간이 만료되었습니다."),
    USER_INACTIVE(3005, HttpStatus.UNAUTHORIZED, "비활성화된 사용자입니다."),
    NOT_MATCH_TOKEN_PAIR(3006, HttpStatus.UNAUTHORIZED, "사용자 정보가 일치하지 않습니다."),

    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    EMAIL_EXCEPTION(20001, HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생하였습니다."),

    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;


    /**
     * 예상하지 못한 에러가 발생한 경우 httpStatus 에 따라 ErrorCode 를 생성해주기 위함
     * @param httpStatus
     * @return
     */
    public static ErrorCode valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) { throw new GeneralException("HttpStatus is null."); }

        // ErrorCode 전체를 조회하여 httpStatus 와 동일한 값을 탐색
        // 존재하지 않으면 3xx, 4xx 에 맞는 에러코드 리턴
        // 3xx, 4xx 가 아니면 200 리턴
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) { return ErrorCode.BAD_REQUEST; }
                    else if (httpStatus.is5xxServerError()) { return ErrorCode.INTERNAL_ERROR; }
                    else { return ErrorCode.OK; }
                });
    }

    public String getMessage(Throwable e) {
//        return this.getMessage(this.getMessage() + " - " + e.getMessage());
        return this.getMessage(e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}

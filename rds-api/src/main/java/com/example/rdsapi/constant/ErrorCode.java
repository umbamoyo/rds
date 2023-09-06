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
    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
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

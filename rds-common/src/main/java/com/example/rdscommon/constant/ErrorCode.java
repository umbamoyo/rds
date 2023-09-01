package com.example.rdscommon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS("0000","SUCCESS"),
    UNKNOWN_ERROR("9999","UNKNOWN_ERROR")
    ;

    private String code;
    private String message;
}

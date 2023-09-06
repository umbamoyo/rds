package com.example.rdsapi.dto.response.common;


import com.example.rdsapi.constant.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiDataResponse<T> extends ApiErrorResponse {

    private final T data;

    private ApiDataResponse(T data) {
        super(true, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        this.data = data;
    }
    private ApiDataResponse(Integer errorCode, String customMessage, T data) {
        super(true, errorCode, customMessage);
        this.data = data;
    }
    private ApiDataResponse(String customMessage, T data) {
        super(true, ErrorCode.OK.getCode(), customMessage);
        this.data = data;
    }

    public static <T> ApiDataResponse<T> of(T data) {
        return new ApiDataResponse<>(data);
    }
    public static <T> ApiDataResponse<T> of(Integer errorCode, String customMessage, T data) {
        return new ApiDataResponse<>(errorCode,customMessage,data);
    }

    public static <T> ApiDataResponse<T> emptyWithCustomMessage(String customMessage) {
        return new ApiDataResponse<>(customMessage, null);
    }

    public static <T> ApiDataResponse<T> empty() {
        return new ApiDataResponse<>(null);
    }

    // 단순 성공 결과값 리턴시
    public static ApiDataResponse<GeneralResponse> success(){
        return new ApiDataResponse<>(new GeneralResponse(true));
    }

    // 단순 실패 결과값 리턴시
    public static ApiDataResponse<GeneralResponse> failed(){
        return new ApiDataResponse<>(new GeneralResponse(false));
    }

    @Getter
    @AllArgsConstructor
    public static class GeneralResponse{
        private boolean success;
    }

}
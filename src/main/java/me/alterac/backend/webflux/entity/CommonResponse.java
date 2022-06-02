package me.alterac.backend.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";

    private String msg;
    private String stacktrace;
    private T data;

    public static <T> CommonResponse<T> createSuccessResponse(T data) {
        return CommonResponse.<T>builder()
                .msg(SUCCESS)
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> createFailedResponse(T data) {
        return CommonResponse.<T>builder()
                .msg(FAILED)
                .data(data)
                .build();
    }

    public static CommonResponse<String> createExceptionResponse(Throwable throwable, boolean printStackTrace) {
        return CommonResponse.<String>builder()
                .msg(FAILED)
                .data(throwable.getMessage())
                .stacktrace(printStackTrace ? ExceptionUtils.getStackTrace(throwable) : "")
                .build();
    }

    public static CommonResponse<Void> defaultSuccessResponse() {
        return CommonResponse.<Void>builder()
                .msg(SUCCESS)
                .build();
    }

    public static CommonResponse<Void> defaultFailedResponse() {
        return CommonResponse.<Void>builder()
                .msg(FAILED)
                .build();
    }
}

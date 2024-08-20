package com.cjhdev.cms.order.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final int status;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
        this.status = errorCode.getHttpStatus().value();
    }

    @AllArgsConstructor
    @Builder
    @Getter
    public static class CustomExceptionResponse{
        private int status;
        private String code;
        private String message;
    }
}

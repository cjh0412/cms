package com.cjhdev.cms.user.exception;

import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({CustomerException.class})
    public ResponseEntity<ExceptionResponse> handleCustomerException(final CustomerException e) {
        log.warn("api exception : {}" , e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(e.getMessage(), e.getErrorCode()));
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ExceptionResponse{
        private String message;
        private ErrorCode errorCode;
    }
}

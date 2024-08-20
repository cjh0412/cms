package com.cjhdev.cms.order.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionAdvice {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomException.CustomExceptionResponse> handleCustomException(final CustomException e) {
        log.warn("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.warn("api exception : {}" , e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(CustomException.CustomExceptionResponse.builder()
                        .message(e.getMessage())
                        .code(e.getErrorCode().name())
                        .status(e.getStatus())
                        .build());
    }

}

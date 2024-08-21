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
    //@ExceptionHandler 주의사항
    // 선언되어있는 exceptionClass는  문제가 되지 않지만,
    // 그외 HttpHeaders, HttpStatus 등 같이 선언하여 사용하실 때에는 오류가 발생하므로 작성시 주의!

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

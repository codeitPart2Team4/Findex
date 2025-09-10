package com.codeit.findex.common.error;

import com.codeit.findex.common.error.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        HttpStatus httpStatus = e.getBaseErrorCode().getHttpStatus();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                httpStatus.value(),
                e.getBaseErrorCode().getMessage(),
                e.getDetail()
        );

        return ResponseEntity
                .status(httpStatus)
                .body(errorResponse);
    }
}


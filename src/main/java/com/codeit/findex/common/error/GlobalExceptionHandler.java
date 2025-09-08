package com.codeit.findex.common.error;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
    var msg = e.getBindingResult().getAllErrors().stream()
        .findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage).orElse("Validation error");
    return ResponseEntity.badRequest()
        .body(ApiError.of(HttpStatus.BAD_REQUEST, msg, "VALIDATION_ERROR"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.badRequest()
        .body(ApiError.of(HttpStatus.BAD_REQUEST, e.getMessage(), "BAD_REQUEST"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneral(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "INTERNAL_ERROR"));
  }
}

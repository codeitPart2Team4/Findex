package com.codeit.findex.common.error;

import org.springframework.http.HttpStatus;

public record ApiError(
    String message,
    String code,
    int status
) {
  public static ApiError of(HttpStatus status, String message, String code) {
    return new ApiError(message, code, status.value());
  }
}

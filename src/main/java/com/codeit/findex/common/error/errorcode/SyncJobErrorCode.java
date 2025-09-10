package com.codeit.findex.common.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SyncJobErrorCode implements BaseErrorCode {

    // 400
    ASYNC_JOB_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ASYNC_JOB_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜 범위입니다."),
    ASYNC_JOB_INVALID_FILTER(HttpStatus.BAD_REQUEST, "유효하지 않은 필터 값입니다."),

    // 404
    ASYNC_JOB_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "지수 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

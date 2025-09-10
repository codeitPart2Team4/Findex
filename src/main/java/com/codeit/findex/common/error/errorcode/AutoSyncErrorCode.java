package com.codeit.findex.common.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AutoSyncErrorCode implements BaseErrorCode {

    // 400
    AUTO_SYNC_INVALID_SETTING(HttpStatus.BAD_REQUEST, "유효하지 않은 설정 값입니다."),
    AUTO_SYNC_INVALID_FILTER(HttpStatus.BAD_REQUEST, "유효하지 않은 필터 값입니다."),

    // 404
    AUTO_SYNC_NOT_FOUND(HttpStatus.NOT_FOUND, "자동 연동 설정을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

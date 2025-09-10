package com.codeit.findex.common.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IndexInfoErrorCode implements BaseErrorCode {

    // 400
    INDEX_INFO_DUPLICATED(HttpStatus.BAD_REQUEST, "지수 정보가 중복입니다."),
    INDEX_INFO_REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "필수 필드가 누락되었습니다."),
    INDEX_INFO_INVALID_FIELD(HttpStatus.BAD_REQUEST, "유효하지 않은 필드 값입니다."),
    INDEX_INFO_INVALID_FILTER(HttpStatus.BAD_REQUEST, "유효하지 않은 필터 값입니다."),

    // 404
    INDEX_INFO_NOTFOUND(HttpStatus.NOT_FOUND, "지수 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

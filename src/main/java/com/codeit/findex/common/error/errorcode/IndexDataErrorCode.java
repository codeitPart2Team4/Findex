package com.codeit.findex.common.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IndexDataErrorCode implements BaseErrorCode {

    // 400
    INDEX_DATA_DUPLICATED(HttpStatus.BAD_REQUEST, "지수 데이터가 중복입니다."),
    INDEX_DATA_INVALID_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 데이터 값입니다."),
    INDEX_DATA_INVALID_FILTER(HttpStatus.BAD_REQUEST, "유효하지 않은 필터 값입니다."),
    INDEX_DATA_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "유효하지 않은 기간 유형입니다."),

    // 404
    INDEX_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "지수 데이터를 찾을 수 없습니다."),
    INDEX_DATA_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "참조하는 지수 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

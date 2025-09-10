package com.codeit.findex.common.error.exception;

import com.codeit.findex.common.error.errorcode.BaseErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final BaseErrorCode baseErrorCode;
    private String detail;

    public BaseException(BaseErrorCode baseErrorCode) {
        this.baseErrorCode = baseErrorCode;
    }

    public BaseException(BaseErrorCode baseErrorCode, String detail) {
        this.baseErrorCode = baseErrorCode;
        this.detail = detail;
    }
}

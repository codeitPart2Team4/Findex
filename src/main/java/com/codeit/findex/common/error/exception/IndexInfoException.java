package com.codeit.findex.common.error.exception;

import com.codeit.findex.common.error.errorcode.IndexInfoErrorCode;

public class IndexInfoException extends BaseException {

    public IndexInfoException(IndexInfoErrorCode indexInfoErrorCode) {
        super(indexInfoErrorCode);
    }

    public IndexInfoException(IndexInfoErrorCode indexInfoErrorCode, String detail) {
        super(indexInfoErrorCode, detail);
    }
}

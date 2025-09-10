package com.codeit.findex.common.error.exception;

import com.codeit.findex.common.error.errorcode.IndexDataErrorCode;

public class IndexDataException extends BaseException {

    public IndexDataException(IndexDataErrorCode indexDataErrorCode) {
        super(indexDataErrorCode);
    }

    public IndexDataException(IndexDataErrorCode indexDataErrorCode, String detail) {
        super(indexDataErrorCode, detail);
    }
}
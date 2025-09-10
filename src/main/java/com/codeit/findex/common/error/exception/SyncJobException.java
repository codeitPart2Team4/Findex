package com.codeit.findex.common.error.exception;

import com.codeit.findex.common.error.errorcode.SyncJobErrorCode;

public class SyncJobException extends BaseException {

    public SyncJobException(SyncJobErrorCode syncJobErrorCode) {
        super(syncJobErrorCode);
    }

    public SyncJobException(SyncJobErrorCode syncJobErrorCode, String detail) {
        super(syncJobErrorCode, detail);
    }
}
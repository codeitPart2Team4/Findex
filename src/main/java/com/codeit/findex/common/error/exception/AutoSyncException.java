package com.codeit.findex.common.error.exception;

import com.codeit.findex.common.error.errorcode.AutoSyncErrorCode;

public class AutoSyncException extends BaseException {

    public AutoSyncException(AutoSyncErrorCode autoSyncErrorCode) {
        super(autoSyncErrorCode);
    }

    public AutoSyncException(AutoSyncErrorCode autoSyncErrorCode, String detail) {
        super(autoSyncErrorCode, detail);
    }
}
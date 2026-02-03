package com.jjld.global.exception.businessexceptions;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

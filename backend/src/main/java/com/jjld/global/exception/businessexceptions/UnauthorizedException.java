package com.jjld.global.exception.businessexceptions;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

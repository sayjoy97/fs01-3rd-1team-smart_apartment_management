package com.jjld.global.exception.businessexceptions;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class BadRequestException extends BusinessException {
    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

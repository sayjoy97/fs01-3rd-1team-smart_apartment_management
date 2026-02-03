package com.jjld.global.exception.businessexceptions;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(ErrorCode.NOT_FOUND);
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
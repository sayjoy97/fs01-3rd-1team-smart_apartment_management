package com.jjld.global.exception.businessexceptions;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class UnprocessableEntityException extends BusinessException {
    public UnprocessableEntityException() {
        super(ErrorCode.UNPROCESSABLE_ENTITY);
    }

    public UnprocessableEntityException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

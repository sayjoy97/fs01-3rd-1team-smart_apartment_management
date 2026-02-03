package com.jjld.global.exception.businessexceptions;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class UnprocessableEntityException extends BusinessException {
    public UnprocessableEntityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnprocessableEntityException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

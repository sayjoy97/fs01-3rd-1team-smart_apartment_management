package com.jjld.global.exception.complaint;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class ComplaintNotFoundException extends BusinessException {
    public ComplaintNotFoundException() {
        super(ErrorCode.COMPLAINT_NOT_FOUND);
    }

    public ComplaintNotFoundException(String message) {
        super(ErrorCode.COMPLAINT_NOT_FOUND, message);

    }
}

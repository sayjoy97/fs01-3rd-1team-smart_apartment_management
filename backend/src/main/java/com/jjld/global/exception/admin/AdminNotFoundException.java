package com.jjld.global.exception.admin;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class AdminNotFoundException extends BusinessException {
    public AdminNotFoundException() {
        super(ErrorCode.ADMIN_NOT_FOUND);
    }

    public AdminNotFoundException(String message) {
        super(ErrorCode.ADMIN_NOT_FOUND, message);
    }
}

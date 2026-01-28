package com.jjld.global.exception.admin;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class DuplicateAdminLoginIdException extends BusinessException {
    public DuplicateAdminLoginIdException() {
        super(ErrorCode.DUPLICATE_ADMIN_LOGIN_ID);
    }

    public DuplicateAdminLoginIdException(String message) {
        super(ErrorCode.DUPLICATE_ADMIN_LOGIN_ID, message);
    }
}

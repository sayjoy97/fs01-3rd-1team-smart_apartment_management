package com.jjld.global.exception.house;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class UserAccountNotFound extends BusinessException {
    public UserAccountNotFound(String message) {
        super(ErrorCode.USER_ACCOUNT_NOT_FOUND, message);
    }
}

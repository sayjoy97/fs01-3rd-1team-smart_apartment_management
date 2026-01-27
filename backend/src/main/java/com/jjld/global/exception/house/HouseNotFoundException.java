package com.jjld.global.exception.house;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class HouseNotFoundException extends BusinessException {
    public HouseNotFoundException() {
        super(ErrorCode.HOUSE_NOT_FOUND);
    }

    public HouseNotFoundException(String message) {
        super(ErrorCode.HOUSE_NOT_FOUND, message);

    }
}

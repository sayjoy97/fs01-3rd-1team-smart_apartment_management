package com.jjld.global.exception.house;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class HouseCardNotFoundException extends BusinessException {
    public HouseCardNotFoundException(String message) {
        super(ErrorCode.HOUSE_CARD_NOT_FOUND, message);
    }
}

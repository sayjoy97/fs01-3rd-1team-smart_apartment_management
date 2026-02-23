package com.jjld.global.exception.house;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class CardAlreadyAssigned extends BusinessException {
    public CardAlreadyAssigned(String message) {
        super(ErrorCode.CARD_ALREADY_ASSIGNED, message);
    }
}

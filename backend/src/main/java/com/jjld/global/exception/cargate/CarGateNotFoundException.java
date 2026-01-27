package com.jjld.global.exception.cargate;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class CarGateNotFoundException extends BusinessException {
    public CarGateNotFoundException() {
        super(ErrorCode.CAR_GATE_NOT_FOUND);
    }

    public CarGateNotFoundException(String message) {
        super(ErrorCode.CAR_GATE_NOT_FOUND, message);

    }
}

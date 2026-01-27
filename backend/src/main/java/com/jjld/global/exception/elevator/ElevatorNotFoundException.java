package com.jjld.global.exception.elevator;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class ElevatorNotFoundException extends BusinessException {
    public ElevatorNotFoundException() {
        super(ErrorCode.ELEVATOR_NOT_FOUND);
    }

    public ElevatorNotFoundException(String message) {
        super(ErrorCode.ELEVATOR_NOT_FOUND, message);

    }
}

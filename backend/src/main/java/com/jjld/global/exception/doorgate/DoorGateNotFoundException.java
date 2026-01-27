package com.jjld.global.exception.doorgate;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class DoorGateNotFoundException extends BusinessException {
    public DoorGateNotFoundException() {
        super(ErrorCode.DOOR_GATE_NOT_FOUND);
    }

    public DoorGateNotFoundException(String message) {
        super(ErrorCode.DOOR_GATE_NOT_FOUND, message);

    }
}

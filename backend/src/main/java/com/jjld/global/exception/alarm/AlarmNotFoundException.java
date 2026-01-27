package com.jjld.global.exception.alarm;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class AlarmNotFoundException extends BusinessException {
    public AlarmNotFoundException() {
        super(ErrorCode.ALARM_NOT_FOUND);
    }

    public AlarmNotFoundException(String message) {
        super(ErrorCode.ALARM_NOT_FOUND, message);
    }
}

package com.jjld.global.exception.parkingfee;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class ParkingFeeAlarmNotFoundException extends BusinessException {
    public ParkingFeeAlarmNotFoundException() {
        super(ErrorCode.PARKING_FEE_NOT_FOUND);
    }

    public ParkingFeeAlarmNotFoundException(String message) {
        super(ErrorCode.PARKING_FEE_NOT_FOUND, message);

    }
}

package com.jjld.global.exception.garden;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class GardenNotFoundException extends BusinessException {
    public GardenNotFoundException() {
        super(ErrorCode.GARDEN_NOT_FOUND);
    }

    public GardenNotFoundException(String message) {
        super(ErrorCode.GARDEN_NOT_FOUND, message);

    }
}

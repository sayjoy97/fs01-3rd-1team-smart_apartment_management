package com.jjld.global.exception.noise;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class NoiseNotFoundException extends BusinessException {
    public NoiseNotFoundException() {
        super(ErrorCode.NOISE_NOT_FOUND);
    }

    public NoiseNotFoundException(String message) {
        super(ErrorCode.NOISE_NOT_FOUND, message);

    }
}

package com.jjld.global.exception.complaint;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class ComplaintAlreadyAnswer extends BusinessException {
    public ComplaintAlreadyAnswer(String message)
    {
        super(ErrorCode.COMPLAINT_ALREADY_ANSWER, message);
    }
}
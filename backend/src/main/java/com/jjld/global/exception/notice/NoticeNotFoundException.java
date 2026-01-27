package com.jjld.global.exception.notice;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;

public class NoticeNotFoundException extends BusinessException {
    public NoticeNotFoundException() {
        super(ErrorCode.NOTICE_NOT_FOUND);
    }

    public NoticeNotFoundException(String message) {
        super(ErrorCode.NOTICE_NOT_FOUND, message);

    }
}

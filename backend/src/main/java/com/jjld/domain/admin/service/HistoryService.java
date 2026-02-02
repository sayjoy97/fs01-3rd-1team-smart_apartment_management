package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dto.HistoryRes;
import com.jjld.domain.admin.dto.HistorySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    Page<HistoryRes> getAccessLogs(Long adminId, HistorySearchCondition cond, Pageable pageable);
}

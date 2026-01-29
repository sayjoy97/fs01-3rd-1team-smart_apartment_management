package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dao.HistoryDAO;
import com.jjld.domain.admin.dto.HistoryRes;
import com.jjld.domain.admin.dto.HistorySearchCondition;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.History;
import com.jjld.domain.admin.specification.HistorySpecification;
import com.jjld.global.exception.admin.AdminNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final AdminDAO adminDAO;
    private final HistoryDAO historyDAO;
    private final ModelMapper modelMapper;

    // 관리자 접속 기록 조회
    @Override
    public Page<HistoryRes> getAccessLogs(Long adminId, HistorySearchCondition cond, Pageable pageable) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());
        Specification<History> spec = HistorySpecification.withCondition(admin, cond);
        Page<History> histories = historyDAO.getAccessLogs(spec, pageable);
        Page<HistoryRes> response = histories
                .map(history -> {
                    HistoryRes historyRes = modelMapper.map(history, HistoryRes.class);
                    historyRes.setAdminId(history.getAdmin().getAdminId());

                    return historyRes;
                });

        return response;
    }
}

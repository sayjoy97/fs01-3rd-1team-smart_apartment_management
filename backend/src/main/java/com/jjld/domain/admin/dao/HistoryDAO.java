package com.jjld.domain.admin.dao;

import com.jjld.domain.admin.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface HistoryDAO {
    void createLog(History history);

    Page<History> getAccessLogs(Specification<History> spec, Pageable pageable);
}

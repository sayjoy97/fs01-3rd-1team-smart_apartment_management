package com.jjld.domain.admin.dao;

import com.jjld.domain.admin.entity.History;
import com.jjld.domain.admin.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistoryDAOImpl implements HistoryDAO {
    private final HistoryRepository historyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLog(History history) {
        historyRepository.save(history);
    }

    @Override
    public Page<History> getAccessLogs(Specification<History> spec, Pageable pageable) {
        return historyRepository.findAll(spec, pageable);
    }
}

package com.jjld.domain.admin.repository;

import com.jjld.domain.admin.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistoryRepository extends JpaRepository<History, Long>, JpaSpecificationExecutor<History> {
}

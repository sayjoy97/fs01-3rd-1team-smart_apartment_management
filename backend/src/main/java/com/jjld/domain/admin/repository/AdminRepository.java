package com.jjld.domain.admin.repository;

import com.jjld.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
    // adminLoginId를 이용해 관리자 조회
    Optional<Admin> findByAdminLoginId(String adminLoginId);

    // 활동 중인 관리자 수 조회
    long countByStateTrue();

    // 신규 관리자 수 조회
    long countByCreatedAtAfter(LocalDateTime startOfMonth);
}

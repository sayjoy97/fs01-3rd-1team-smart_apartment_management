package com.jjld.domain.admin.repository;

import com.jjld.domain.admin.entity.Admin;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
    // adminLoginId를 이용해 관리자 조회
    Optional<Admin> findByAdminLoginId(String adminLoginId);
}

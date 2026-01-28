package com.jjld.domain.admin.repository;

import com.jjld.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAdminLoginId(String adminLoginId);
}

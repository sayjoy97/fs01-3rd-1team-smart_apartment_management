package com.jjld.domain.admin.dao;

import com.jjld.domain.admin.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminDAO {
    Optional<Admin> getAdmin(Long adminId);

    void createAdmin(Admin admin);

    Optional<Admin> findByAdminLoginId(String adminLoginId);

    void deleteAdmin(Long adminId);

    List<Admin> getAdmins();
}

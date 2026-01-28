package com.jjld.domain.admin.dao;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminDAOImpl implements AdminDAO {
    private final AdminRepository adminRepository;

    @Override
    public Optional<Admin> getAdmin(Long adminId) {
        return adminRepository.findById(adminId);
    }

    @Override
    public void createAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    @Override
    public Optional<Admin> findByAdminLoginId(String adminLoginId) {
        return adminRepository.findByAdminLoginId(adminLoginId);
    }
}

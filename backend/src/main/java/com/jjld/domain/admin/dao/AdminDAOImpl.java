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

    // adminId를 이용해 관리자 조회
    @Override
    public Optional<Admin> getAdmin(Long adminId) {
        return adminRepository.findById(adminId);
    }

    // 관리자 추가
    @Override
    public void createAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    // adminLoginId를 이용해 관리자 조회
    @Override
    public Optional<Admin> findByAdminLoginId(String adminLoginId) {
        return adminRepository.findByAdminLoginId(adminLoginId);
    }

    // adminId를 이용해 관리자 삭제
    @Override
    public void deleteAdmin(Long adminId) {
        adminRepository.deleteById(adminId);
    }
}

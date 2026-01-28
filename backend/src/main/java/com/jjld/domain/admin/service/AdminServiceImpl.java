package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dto.AdminReq;
import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.global.exception.admin.AdminNotFoundException;
import com.jjld.global.exception.admin.DuplicateAdminLoginIdException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;

    // adminId를 이용해 관리자 반환
    @Override
    public AdminRes getAdmin(Long adminId) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());

        AdminRes response = modelMapper.map(admin, AdminRes.class);

        return response;
    }

    // 관리자 추가
    @Override
    public void createAdmin(AdminReq adminReq) {
        if (adminDAO.findByAdminLoginId(adminReq.getAdminLoginId()).isPresent()) {
            throw new DuplicateAdminLoginIdException();
        }

        Admin admin = modelMapper.map(adminReq, Admin.class);
        adminDAO.createAdmin(admin);
    }

    // adminId를 이용해 관리자 삭제
    @Override
    public void deleteAdmin(Long adminId) {
        if (adminDAO.getAdmin(adminId).isEmpty()) {
            throw new AdminNotFoundException();
        }

        adminDAO.deleteAdmin(adminId);
    }

    // 관리자 목록을 조회
    @Override
    public List<AdminRes> getAdmins() {
        List<Admin> admins = adminDAO.getAdmins();
        List<AdminRes> response = modelMapper.map(admins, List.class);
        return response;
    }
}

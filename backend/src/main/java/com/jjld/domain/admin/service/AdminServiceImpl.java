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

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;

    @Override
    public AdminRes getAdmin(Long adminId) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());

        AdminRes adminResDTO = modelMapper.map(admin, AdminRes.class);

        return adminResDTO;
    }

    @Override
    public void createAdmin(AdminReq adminReq) {
        if (adminDAO.findByAdminLoginId(adminReq.getAdminLoginId()).isPresent()) {
            throw new DuplicateAdminLoginIdException("test");
        }

        Admin admin = modelMapper.map(adminReq, Admin.class);
        adminDAO.createAdmin(admin);
    }
}

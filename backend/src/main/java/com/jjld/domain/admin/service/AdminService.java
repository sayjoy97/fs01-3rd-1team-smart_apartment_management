package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dto.AdminReq;
import com.jjld.domain.admin.dto.AdminRes;

public interface AdminService {
    AdminRes getAdmin(Long adminId);

    void createAdmin(AdminReq adminReq);

    void deleteAdmin(Long adminId);
}

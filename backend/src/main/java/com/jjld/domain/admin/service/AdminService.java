package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    MyPageRes getAdmin(Long adminId);

    void createAdmin(AdminReq adminReq);

    void deleteAdmin(Long adminId, Long targetAdminId, DeleteReq deleteReq);

    List<AdminRes> getAdmins();

    Page<AdminRes> getAdmins(AdminSearchCondition cond, Pageable pageable);

    AdminsStatsRes getAdminsStats();

    void updateAdminAuthority(Long adminId, Long targetAdminId, UpdateAuthorityReq updateAuthorityReq);

    void updateAdmin(Long adminId, UpdateAdminReq updateAdminReq);

    LoginRes loginAdmin(LoginAdminReq loginAdminReq, HttpServletRequest servletRequest);

    TokenRes refresh(String refreshToken);

    void initialSetupAdmin(Long adminId, SetupAdminReq setupAdminReq, HttpServletRequest servletRequest);

    void logoutAdmin(Long adminId, HttpServletRequest servletRequest);

    void findPass(FindPassReq findPassReq);

    void changePass(@Valid ChangePassReq changePassReq);
}

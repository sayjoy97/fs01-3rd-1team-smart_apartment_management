package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dto.AdminReq;
import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.dto.AdminSearchCondition;
import com.jjld.domain.admin.dto.UpdateAdminReq;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.admin.specification.AdminSpecification;
import com.jjld.global.exception.admin.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;

import static com.jjld.global.exception.ErrorCode.PASSWORD_MISMATCH;

@Service
@RequiredArgsConstructor
@Builder
public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final AdminRepository adminRepository;

    // adminId를 이용해 관리자 조회
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

        if (!adminReq.getAdminPass().equals(adminReq.getConfirmPass())) {
            throw new PasswordMismatchException();
        }

        Admin admin = Admin.builder()
                .adminLoginId(adminReq.getAdminLoginId())
                .adminPass(encoder.encode(adminReq.getAdminPass()))
                .state(false)
                .adminRole(adminReq.getAdminRole())
                .build();

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

    // 관리자 목록 필터 조회
    @Override
    public Page<AdminRes> getAdmins(AdminSearchCondition cond, Pageable pageable) {
        Specification<Admin> spec = AdminSpecification.withCondition(cond);
        Page<Admin> admins = adminDAO.getAdmins(spec, pageable);
        Page<AdminRes> response = admins.map(admin -> modelMapper.map(admin, AdminRes.class));

        return response;
    }

    @Override
    public void updateAdminAuthority(Long adminId, Long targetAdminId, AdminRole adminRole) {
        Admin superAdmin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());

        if (
                !(superAdmin.getAdminRole().equals(AdminRole.SUPER_ADMIN) ||
                superAdmin.getAdminRole().equals(AdminRole.ACTING_ADMIN))
        ) {
            throw new SuperAdminOnlyException();
        }

        Admin targetAdmin = adminDAO.getAdmin(targetAdminId)
                .orElseThrow(() -> new AdminNotFoundException());

        targetAdmin.setAdminRole(adminRole);
        adminDAO.updateAdminAuthority(targetAdmin);
    }

    @Override
    public void updateAdmin(Long adminId, UpdateAdminReq updateAdminReq) {
        Admin Admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());

        // 입력한 현재 비밀번호와 DB에 저장된 비밀번호 비교
        if (!encoder.matches(updateAdminReq.getCurrentPassword(), Admin.getAdminPass())) {
            throw new InvalidCurrentPassword();
        }

        // 현재 비밀번호와 새 비밀번호 비교
        if (updateAdminReq.getCurrentPassword().equals(updateAdminReq.getNewPassword())) {
            throw new SameAsOldPassword();
        }

        // 새 비밀번호와 새 비밀번호 확인 비교
        if (!updateAdminReq.getNewPassword().equals(updateAdminReq.getConfirmNewPassword())) {
            throw new PasswordMismatchException();
        }

        Admin.setAdminName(updateAdminReq.getAdminName());
        Admin.setAdminPass(encoder.encode(updateAdminReq.getNewPassword()));
        Admin.setAdminPhone(updateAdminReq.getAdminPhone());
        Admin.setAdminEmail(updateAdminReq.getAdminEmail());

        adminDAO.updateAdmin(Admin);
    }
}

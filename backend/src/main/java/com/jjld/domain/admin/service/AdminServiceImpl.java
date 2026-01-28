package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dto.*;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.admin.specification.AdminSpecification;
import com.jjld.global.exception.admin.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Builder
@Slf4j
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
        List<AdminRes> response = adminDAO.getAdmins()
                .stream()
                .map(A -> modelMapper.map(A, AdminRes.class))
                .collect(Collectors.toList());

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

    // 관리자 권한 수정
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

    // 관리자 정보 수정
    @Override
    public void updateAdmin(Long adminId, UpdateAdminReq updateAdminReq) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());

        // 입력한 현재 비밀번호와 DB에 저장된 비밀번호 비교
        if (!encoder.matches(updateAdminReq.getCurrentPassword(), admin.getAdminPass())) {
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

        admin.setAdminName(updateAdminReq.getAdminName());
        admin.setAdminPass(encoder.encode(updateAdminReq.getNewPassword()));
        admin.setAdminPhone(updateAdminReq.getAdminPhone());
        admin.setAdminEmail(updateAdminReq.getAdminEmail());

        adminDAO.updateAdmin(admin);
    }

    @Override
    public LoginAdminRes loginAdmin(LoginAdminReq loginAdminReq) {
        Admin admin = adminDAO.findByAdminLoginId(loginAdminReq.getAdminLoginId()).orElse(null);
        boolean isException = false;

        if (admin == null) {
            isException = true;
            log.info("아이디가 존재하지 않습니다.");
        }

        if (!encoder.matches(loginAdminReq.getAdminPass(), admin.getAdminPass())) {
            isException = true;
            log.info("비밀번호가 틀렸습니다.");
        }

        if (!isException) {
            throw new AdminNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (admin.getIsFirstLogin()) admin.setState(true);
        adminDAO.updateAdmin(admin);

        LoginAdminRes response = modelMapper.map(admin, LoginAdminRes.class);

        return response;
    }
}

package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dao.HistoryDAO;
import com.jjld.domain.admin.dto.*;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AccessType;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.admin.entity.History;
import com.jjld.domain.admin.security.AdminUserDetail;
import com.jjld.domain.admin.specification.AdminSpecification;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.exception.businessexceptions.ConflictException;
import com.jjld.global.exception.businessexceptions.ForbiddenException;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import com.jjld.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;
    private final HistoryDAO historyDAO;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;

    // adminId를 이용해 관리자 조회
    @Override
    public AdminRes getAdmin(Long adminId) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "조회할 관리자를 찾을 수 없습니다."));

        AdminRes response = modelMapper.map(admin, AdminRes.class);

        return response;
    }

    // 관리자 추가
    @Override
    public void createAdmin(AdminReq adminReq) {
        if (adminDAO.findByAdminLoginId(adminReq.getAdminLoginId()).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_ADMIN_LOGIN_ID, "이미 사용 중인 관리자 아이디입니다.");
        }

        if (!adminReq.getAdminPass().equals(adminReq.getConfirmPass())) {
            throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Admin admin = Admin.builder()
                .adminLoginId(adminReq.getAdminLoginId())
                .adminPass(encoder.encode(adminReq.getAdminPass()))
                .isFirstLogin(true)
                .state(false)
                .adminRole(adminReq.getAdminRole())
                .build();

        adminDAO.createAdmin(admin);
    }

    // adminId를 이용해 관리자 삭제
    @Override
    public void deleteAdmin(Long adminId) {
        if (adminDAO.getAdmin(adminId).isEmpty()) {
            throw new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "삭제할 관리자를 찾을 수 없습니다.");
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
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "권한을 수정하는 관리자를 찾을 수 없습니다."));

        if (superAdmin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        Admin targetAdmin = adminDAO.getAdmin(targetAdminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "권한이 수정될 관리자를 찾을 수 없습니다."));

        targetAdmin.setAdminRole(adminRole);
        adminDAO.updateAdminAuthority(targetAdmin);
    }

    // 관리자 정보 수정
    @Override
    public void updateAdmin(Long adminId, UpdateAdminReq updateAdminReq) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "정보를 수정할 관리자를 찾을 수 없습니다."));

        // 입력한 현재 비밀번호와 DB에 저장된 비밀번호 비교
        if (!encoder.matches(updateAdminReq.getCurrentPassword(), admin.getAdminPass())) {
            throw new BadRequestException(ErrorCode.CURRENT_PASSWORD_MISMATCH, "현재 비밀번호와 입력한 비밀번호가 일치하지 않습니다.");
        }

        // 현재 비밀번호와 새 비밀번호 비교
        if (updateAdminReq.getCurrentPassword().equals(updateAdminReq.getNewPassword())) {
            throw new BadRequestException(ErrorCode.SAME_AS_OLD_PASSWORD, "새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.");
        }

        // 새 비밀번호와 새 비밀번호 확인 비교
        if (!updateAdminReq.getNewPassword().equals(updateAdminReq.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        admin.setAdminName(updateAdminReq.getAdminName());
        admin.setAdminPass(encoder.encode(updateAdminReq.getNewPassword()));
        admin.setAdminPhone(updateAdminReq.getAdminPhone());
        admin.setAdminEmail(updateAdminReq.getAdminEmail());

        adminDAO.updateAdmin(admin);
    }

    // 관리자 로그인
//    @Override
//    public LoginRes loginAdmin(LoginAdminReq loginAdminReq, HttpServletRequest servletRequest) {
//        try {
//            UsernamePasswordAuthenticationToken token =
//                    new UsernamePasswordAuthenticationToken(loginAdminReq.getAdminLoginId(), loginAdminReq.getAdminPass());
//
//            token.setDetails(loginAdminReq);
//
//            // 인증 수행
//            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
//            AdminUserDetail adminUserDetail = (AdminUserDetail) authentication.getPrincipal();
//
//            String jwtToken = tokenProvider.createToken(authentication);
//
//            String ipAddress = servletRequest.getHeader("X-Forwarded-For");
//            if (ipAddress == null) {
//                ipAddress = servletRequest.getRemoteAddr();
//            }
//
//            Admin admin = adminUserDetail.getAdmin();
//
//            History history = History.builder()
//                    .admin(admin)
//                    .ipAddress(ipAddress)
//                    .accessType(AccessType.LOGIN)
//                    .success(true)
//                    .message("로그인 성공")
//                    .build();
//            historyDAO.createLog(history);
//
//            admin.setState(true);
//            adminDAO.updateAdmin(admin);
//
//            LoginAdminRes loginAdminRes = modelMapper.map(admin, LoginAdminRes.class);
//
//            LoginRes response = LoginRes.builder()
//                    .accessToken(jwtToken)
//                    .username(adminUserDetail.getUsername())
//                    .roles(adminUserDetail.getAuthorities().stream()
//                            .map(GrantedAuthority::getAuthority)
//                            .toList())
//                    .loginAdminRes(loginAdminRes)
//                    .build();
//
//            return response;
//        } catch (exception instanceof BadCredentialsException && admin != null) {
//            throw new NotFoundException(ErrorCode.INVALID_CREDENTIALS, "아이디 또는 비밀번호가 일치하지 않습니다.");
//        } catch (UsernameNotFoundException e) {
//            // 🔥 아이디 or 비번 틀림
//            throw new NotFoundException(ErrorCode.INVALID_CREDENTIALS, "아이디 또는 비밀번호가 일치하지 않습니다.");
//        }
//    }

    // 관리자 최초 로그인 처리
    @Override
    public void initialSetupAdmin(Long adminId, SetupAdminReq setupAdminReq, HttpServletRequest servletRequest) {
        // 프록시, 로드밸런서를 거치면 IP가 프록시 IP로 나올 수 있으므로 X-Forwarded-For 헤더 체크 필요
        String ipAddress = servletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = servletRequest.getRemoteAddr();
        }
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "로그인할 관리자를 찾을 수 없습니다."));

        // 입력한 새 비밀번호와 DB에 저장된 비밀번호 비교
        if (encoder.matches(setupAdminReq.getNewPassword(), admin.getAdminPass())) {
            throw new BadRequestException(ErrorCode.SAME_AS_OLD_PASSWORD, "새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.");
        }

        // 새 비밀번호와 새 비밀번호 확인 비교
        if (!setupAdminReq.getNewPassword().equals(setupAdminReq.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        admin.setAdminName(setupAdminReq.getAdminName());
        admin.setAdminPass(encoder.encode(setupAdminReq.getNewPassword()));
        admin.setAdminPhone(setupAdminReq.getAdminPhone());
        admin.setAdminEmail(setupAdminReq.getAdminEmail());
        admin.setIsFirstLogin(false);

        History history = History.builder()
                .admin(admin)
                .ipAddress(ipAddress)
                .accessType(AccessType.INITIAL_SETUP)
                .success(true)
                .message("최초 설정 성공")
                .build();
        historyDAO.createLog(history);

        adminDAO.updateAdmin(admin);
    }

    // 관리자 로그아웃
    @Override
    public void logoutAdmin(Long adminId, HttpServletRequest servletRequest) {
        // 프록시, 로드밸런서를 거치면 IP가 프록시 IP로 나올 수 있으므로 X-Forwarded-For 헤더 체크 필요
        String ipAddress = servletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = servletRequest.getRemoteAddr();
        }
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "로그아웃할 관리자를 찾을 수 없습니다."));

        admin.setState(false);
        adminDAO.updateAdmin(admin);

        History history = History.builder()
                .admin(admin)
                .ipAddress(ipAddress)
                .accessType(AccessType.LOGOUT)
                .success(true)
                .message("로그아웃 성공")
                .build();
        historyDAO.createLog(history);
    }
}

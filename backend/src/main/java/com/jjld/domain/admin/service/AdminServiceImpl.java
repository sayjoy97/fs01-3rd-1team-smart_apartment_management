package com.jjld.domain.admin.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dao.HistoryDAO;
import com.jjld.domain.admin.dto.*;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AccessType;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.admin.entity.History;
import com.jjld.domain.admin.entity.RefreshToken;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.admin.repository.RefreshTokenRepository;
import com.jjld.domain.admin.security.AdminUserDetail;
import com.jjld.domain.admin.specification.AdminSpecification;
import com.jjld.domain.complaint.repository.ComplaintReplyRepository;
import com.jjld.domain.notice.repository.NoticeRepository;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.*;
import com.jjld.global.security.AdminAuthenticationToken;
import com.jjld.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;
    private final HistoryDAO historyDAO;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManager adminAuthenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ComplaintReplyRepository complaintReplyRepository;
    private final NoticeRepository noticeRepository;
    private final AdminDetailsService adminDetailsService;
    private final AdminRepository adminRepository;
    private final AdminAuthenticationProvider adminAuthenticationProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // adminId를 이용해 관리자 조회
    @Override
    public MyPageRes getAdmin(Long adminId) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "조회할 관리자를 찾을 수 없습니다."));

        String adminRole = "";
        switch (admin.getAdminRole()) {
            case ADMIN -> adminRole = "일반 관리자";
            case SUPER_ADMIN -> adminRole = "총 관리자";
            case ACTING_ADMIN -> adminRole = "총 관리자 대행";
        }

        AdminRes adminRes = AdminRes.builder()
                .adminId(adminId)
                .adminLoginId(admin.getAdminLoginId())
                .adminName(admin.getAdminName())
                .adminPhone(admin.getAdminPhone())
                .adminEmail(admin.getAdminEmail())
                .adminRole(adminRole)
                .state(admin.getState())
                .createdAt(admin.getCreatedAt())
                .build();

        long resolvedComplaintCount = complaintReplyRepository.countByAdmin_AdminId(adminId);
        long postedNoticeCount = noticeRepository.countByAdmin_AdminId(adminId);
        long totalWorkingDays = ChronoUnit.DAYS.between( admin.getCreatedAt().toLocalDate(), LocalDate.now() ) + 1;

        MyPageRes response = new MyPageRes(
                adminRes,
                resolvedComplaintCount,
                postedNoticeCount,
                totalWorkingDays
        );

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
    public void deleteAdmin(Long adminId, Long targetAdminId, DeleteReq deleteReq) {
        Admin superAdmin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "권한을 수정하는 관리자를 찾을 수 없습니다."));

        if (superAdmin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        if (!encoder.matches(deleteReq.getAdminPass(), superAdmin.getAdminPass())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "비밀번호가 일치하지 않습니다.");
        }

        if (adminDAO.getAdmin(targetAdminId).isEmpty()) {
            throw new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "삭제할 관리자를 찾을 수 없습니다.");
        }

        adminDAO.deleteAdmin(targetAdminId);
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

    // 관리자 페이지에서 관리자 통계 조회
    @Override
    public AdminsStatsRes getAdminsStats() {
        LocalDateTime startOfMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .atStartOfDay();
        long totalAdmins = adminDAO.countTotalAdmins();
        long activeAdmins = adminDAO.countActiveAdmins();
        long newAdmins = adminDAO.countNewAdmins(startOfMonth);

        AdminsStatsRes response = new AdminsStatsRes(totalAdmins, activeAdmins, newAdmins);
        return response;
    }

    // 관리자 권한 수정
    @Override
    public void updateAdminAuthority(Long adminId, Long targetAdminId, UpdateAuthorityReq updateAuthorityReq) {
        Admin superAdmin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "권한을 수정하는 관리자를 찾을 수 없습니다."));

        if (superAdmin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        if (!encoder.matches(updateAuthorityReq.getAdminPass(), superAdmin.getAdminPass())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "비밀번호가 일치하지 않습니다.");
        }

        Admin targetAdmin = adminDAO.getAdmin(targetAdminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "권한이 수정될 관리자를 찾을 수 없습니다."));

        targetAdmin.setAdminRole(updateAuthorityReq.getAdminRole());
        adminDAO.updateAdminAuthority(targetAdmin);
    }

    // 관리자 정보 수정
    @Override
    public void updateAdmin(Long adminId, UpdateAdminReq updateAdminReq) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "정보를 수정할 관리자를 찾을 수 없습니다."));

        System.out.println("UpdateAdminReq: " + updateAdminReq);

        if (updateAdminReq.getCurrentPassword() != null && !updateAdminReq.getCurrentPassword().isBlank()) {
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

            admin.setAdminPass(encoder.encode(updateAdminReq.getNewPassword()));
        }

        admin.setAdminName(updateAdminReq.getAdminName());
        admin.setAdminPhone(updateAdminReq.getAdminPhone());
        admin.setAdminEmail(updateAdminReq.getAdminEmail());

        adminDAO.updateAdmin(admin);
    }

    // 관리자 로그인
    @Override
    public LoginRes loginAdmin(LoginAdminReq loginAdminReq, HttpServletRequest servletRequest) {
        try {
            AdminAuthenticationToken token = new AdminAuthenticationToken (
                    loginAdminReq.getAdminLoginId(),
                    loginAdminReq.getAdminPass()
            );

            token.setDetails(loginAdminReq);

            // 인증 수행
            Authentication authentication = adminAuthenticationProvider.authenticate(token);
            System.out.println("authentication: " + authentication);
            AdminUserDetail adminUserDetail = (AdminUserDetail) authentication.getPrincipal();

            String jwtToken = tokenProvider.createToken(authentication);

            String refreshToken =
                    tokenProvider.createRefreshToken(authentication);

            String ipAddress = servletRequest.getHeader("X-Forwarded-For");
            if (ipAddress == null) {
                ipAddress = servletRequest.getRemoteAddr();
            }

            Admin admin = adminUserDetail.getAdmin();

            refreshTokenRepository.deleteByAdminId(admin.getAdminId());

            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .adminId(admin.getAdminId())
                            .token(refreshToken)
                            .expiryDate(LocalDateTime.now().plusDays(7))
                            .build()
            );

            History history = History.builder()
                    .admin(admin)
                    .ipAddress(ipAddress)
                    .accessType(AccessType.LOGIN)
                    .success(true)
                    .message("로그인 성공")
                    .build();
            historyDAO.createLog(history);

            admin.setState(true);
            adminDAO.updateAdmin(admin);

            LoginAdminRes loginAdminRes = modelMapper.map(admin, LoginAdminRes.class);

            LoginRes response = LoginRes.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .username(adminUserDetail.getUsername())
                    .roles(adminUserDetail.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList())
                    .loginAdminRes(loginAdminRes)
                    .build();

            return response;

        } catch (Exception e) {
            log.error("잡힌 예외 클래스: {}", e.getClass().getName());
            log.error("에러 메시지: {}", e.getMessage());

            // 1. IP 주소 추출 (공통)
            String ipAddress = servletRequest.getHeader("X-Forwarded-For");
            if (ipAddress == null) ipAddress = servletRequest.getRemoteAddr();

            // 2. 관리자 정보 조회 (실패 기록용)
            Admin admin = adminRepository.findByAdminLoginId(loginAdminReq.getAdminLoginId()).orElse(null);

            // 3. 실패 로그 저장
            try {
                historyDAO.createLog(History.builder()
                        .admin(admin) // admin이 null이어도 로그는 생성되어야 함
                        .ipAddress(ipAddress)
                        .accessType(AccessType.LOGIN)
                        .success(false)
                        .message(admin == null ? "존재하지 않는 아이디로 로그인 시도" : "비밀번호 불일치")
                        .build());
            } catch (Exception logEx) {
                log.error("로그 생성 중 에러 발생 (무시하고 예외 던짐): {}", logEx.getMessage());
            }

            // 4. 공통 예외로 변환하여 던지기
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 토큰 재발급
    public TokenRes refresh(String refreshToken) {
        log.info("RefreshToken={}", refreshToken);

        RefreshToken savedToken =
                refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() ->
                                new NotFoundException(ErrorCode.INVALID_TOKEN, "저장된 토큰이 없습니다."));

        if (savedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(savedToken);
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, "Refresh Token 만료됨");
        }

        Admin admin =
                adminDAO.getAdmin(savedToken.getAdminId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "토큰을 갱신할 관리자를 찾을 수 없습니다."));

        Authentication userInfo = new AdminAuthenticationToken(admin.getAdminLoginId(), admin.getAdminRole());

        UserDetails userDetails = adminDetailsService.loadUserByUsername(admin.getAdminLoginId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = tokenProvider.createToken(authentication);
        log.info("New AccessToken={}", newAccessToken);

        return new TokenRes(newAccessToken);
    }

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

    // 비밀번호 찾기
    @Override
    public void findPass(FindPassReq findPassReq) {
        Admin admin = adminDAO.findByAdminLoginId(findPassReq.getAdminLoginId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "해당 아이디의 관리자를 찾을 수 없습니다."));

        if (!admin.getAdminEmail().equals(findPassReq.getAdminEmail())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "이메일이 일치하지 않습니다.");
        }
    }

    // 비밀번호 변경
    @Override
    public void changePass(ChangePassReq changePassReq) {
        Admin admin = adminDAO.findByAdminLoginId(changePassReq.getAdminLoginId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "해당 아이디의 관리자를 찾을 수 없습니다."));

        if (!changePassReq.getNewPassword().equals(changePassReq.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        admin.setAdminPass(encoder.encode(changePassReq.getNewPassword()));
        adminDAO.updateAdmin(admin);
    }
}

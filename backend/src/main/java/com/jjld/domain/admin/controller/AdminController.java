package com.jjld.domain.admin.controller;

import com.jjld.domain.admin.dto.*;
import com.jjld.domain.admin.repository.RefreshTokenRepository;
import com.jjld.domain.admin.service.AdminService;
import com.jjld.domain.admin.service.HistoryService;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final HistoryService historyService;
    private final RefreshTokenRepository refreshTokenRepository;

    // adminId를 이용해 관리자 조회
    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdmin(@PathVariable("adminId") Long adminId) {
        MyPageRes response = adminService.getAdmin(adminId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 관리자 페이지에서 관리자 추가
    @PostMapping
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminReq adminReq) {
        adminService.createAdmin(adminReq);
        return ResponseEntity.ok(ApiResponse.success("관리자 생성을 성공했습니다."));
    }

    // 관리자 페이지에서 adminId를 이용해 관리자 삭제
    @DeleteMapping("/{adminId}/delete/{targetAdminId}")
    public ResponseEntity<?> deleteAdmin(
            @PathVariable("adminId") Long adminId,
            @PathVariable("targetAdminId") Long targetAdminId,
            @Valid @RequestBody DeleteReq deleteReq
    ) {
        adminService.deleteAdmin(adminId, targetAdminId, deleteReq);
        return ResponseEntity.ok(ApiResponse.success("관리자 삭제를 성공했습니다."));
    }

    // 관리자 페이지에서 관리자 목록을 조회
    @GetMapping
    public ResponseEntity<?> getAdmins() {
        List<AdminRes> response = adminService.getAdmins();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 관리자 페이지에서 관리자 목록 필터 조회
    @GetMapping("/filter")
    public ResponseEntity<?> getAdmins(AdminSearchCondition cond, Pageable pageable) {
        Page<AdminRes> response = adminService.getAdmins(cond, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 관리자 페이지에서 관리자 통계 조회
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminsStats() {
        AdminsStatsRes response = adminService.getAdminsStats();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 관리자 페이지에서 관리자 권한 수정
    @PutMapping("/{adminId}/authority/{targetAdminId}")
    public ResponseEntity<?> updateAdminAuthority(
            @PathVariable("adminId") Long adminId,
            @PathVariable("targetAdminId") Long targetAdminId,
            @Valid @RequestBody UpdateAuthorityReq updateAuthorityReq
    ) {
        adminService.updateAdminAuthority(adminId, targetAdminId, updateAuthorityReq);
        return ResponseEntity.ok(ApiResponse.success("권한 변경을 성공했습니다."));
    }

    // 마이페이지에서 관리자 정보 수정
    @PutMapping("/{adminId}")
    public ResponseEntity<?> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody UpdateAdminReq updateAdminReq
    ) {
        adminService.updateAdmin(adminId, updateAdminReq);
        return ResponseEntity.ok(ApiResponse.success("정보 수정을 성공했습니다."));
    }

    // 관리자 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginAdminReq loginAdminReq,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        LoginRes response = adminService.loginAdmin(loginAdminReq, servletRequest);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true)
                .secure(false) // https 환경이면 true
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRes>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {

        if (refreshToken == null) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, "토큰이 존재하지 않습니다.");
        }

        TokenRes tokenRes = adminService.refresh(refreshToken);

        return ResponseEntity.ok(ApiResponse.success(tokenRes));
    }

    // 관리자 최초 로그인 시 설정
    @PostMapping("/{adminId}/initial-setup")
    public ResponseEntity<?> initialSetupAdmin(
            @PathVariable("adminId") Long adminId,
            @Valid @RequestBody SetupAdminReq setupAdminReq,
            HttpServletRequest servletRequest
    ) {
        adminService.initialSetupAdmin(adminId, setupAdminReq, servletRequest);
        return ResponseEntity.ok(ApiResponse.success("최초 설정을 성공했습니다."));
    }

    // 관리자 로그아웃
    @PostMapping("/{adminId}/logout")
    public ResponseEntity<?> logoutAdmin(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @PathVariable("adminId") Long adminId,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        adminService.logoutAdmin(adminId, servletRequest);
        System.out.println("logoutAdmin");

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> refreshTokenRepository.delete(token));
        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success("로그아웃을 성공했습니다."));
    }

    // 관리자 접속 기록 조회
    @GetMapping("/{adminId}/access-logs")
    public ResponseEntity<?> getAccessLogs(
            @PathVariable("adminId") Long adminId,
            HistorySearchCondition cond,
            Pageable pageable) {
        Page<HistoryRes> response = historyService.getAccessLogs(adminId, cond, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 비밀번호 찾기
    @PostMapping("/find-pass")
    public ResponseEntity<?> findPass(@Valid @RequestBody FindPassReq findPassReq) {
        adminService.findPass(findPassReq);
        return ResponseEntity.ok(ApiResponse.success("인증을 성공했습니다."));
    }

    // 비밀번호 변경
    @PutMapping("/change-pass")
    public ResponseEntity<?> changePass(@Valid @RequestBody ChangePassReq changePassReq) {
        adminService.changePass(changePassReq);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경을 성공했습니다."));
    }
}

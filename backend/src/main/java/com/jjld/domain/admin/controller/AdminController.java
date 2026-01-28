package com.jjld.domain.admin.controller;

import com.jjld.domain.admin.dto.AdminReq;
import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.service.AdminService;
import com.jjld.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // adminId를 이용해 관리자 반환
    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdmin(@PathVariable("adminId") String adminId) {
        AdminRes response = adminService.getAdmin(Long.parseLong(adminId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 관리자 추가
    @PostMapping
    public ResponseEntity<?> createAdmin(
            @Valid @RequestBody AdminReq adminReq
    ) {
        adminService.createAdmin(adminReq);
        return ResponseEntity.ok(ApiResponse.success("관리자 생성을 성공했습니다."));
    }

    // adminId를 이용해 관리자 삭제
    @DeleteMapping("/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable("adminId") String adminId) {
        adminService.deleteAdmin(Long.parseLong(adminId));
        return ResponseEntity.ok(ApiResponse.success("관리자 삭제를 성공했습니다."));
    }

    // 관리자 목록을 조회.
    @GetMapping
    public ResponseEntity<?> getAdmins() {
        List<AdminRes> response = adminService.getAdmins();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

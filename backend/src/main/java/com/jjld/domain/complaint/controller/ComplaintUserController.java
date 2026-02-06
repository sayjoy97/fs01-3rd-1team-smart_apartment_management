package com.jjld.domain.complaint.controller;

import com.jjld.domain.complaint.dto.user.*;
import com.jjld.domain.complaint.service.ComplaintAdminService;
import com.jjld.domain.complaint.service.ComplaintAdminServiceImpl;
import com.jjld.domain.complaint.service.ComplaintUserService;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/api")
@RequiredArgsConstructor
public class ComplaintUserController {
    private final ComplaintUserService service;

    // 입주민 자신이 등록한 민원 목록 조회
    @GetMapping("/list")
    @Operation(summary = "입주민 등록한 민원 목록 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUserComplaintList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AccountUserDetail userDetail = (AccountUserDetail) authentication.getPrincipal();

        List<ComplaintUserResponse> userComplaint = service.findMyComplaintDetail(userDetail);

        return ResponseEntity.ok(
                ApiResponse.success(userComplaint)
        );
    }

    // 입주민 자신이 등록한 민원 상세 조회
    @GetMapping("/complaints/{complaintId}")
    @Operation(summary = "입주민 등록한 민원 상세 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ComplaintUserDetailResponse>> getUserComplaintDetail(
            @PathVariable Long complaintId
            ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof AccountUserDetail)){
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        AccountUserDetail userDetail = (AccountUserDetail) authentication.getPrincipal();
        String email = userDetail.getHouseholderEmail();
        ComplaintUserDetailResponse response = service.findMyComplaintDetail(complaintId, userDetail, email);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    // 민원 작성 시 참조할 민원 목록 조회
    @GetMapping("/reference/{houseId}")
    @Operation(summary = "입주민 참조할 민원 목록 조회")
    public ResponseEntity<?> getReference(@RequestParam("houseId") Long houseId){
        List<ComplaintReference> reference = service.getReferenceComplaints(houseId);

        return ResponseEntity.ok(
                ApiResponse.success(reference)
        );
    }

    // 입주민 민원 작성
    @PostMapping("/write")
    @Operation(summary = "입주민 민원 작성")
    public ResponseEntity<?> writeComplaint(
            @RequestParam Long houseId,
            @RequestBody ComplaintUserWrite userWrite){
        service.write(houseId, userWrite);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

    // 입주민 민원 삭제
    @DeleteMapping("/delete")
    @Operation(summary = "관리자 답변 전 민원 삭제")
    public ResponseEntity<?> deleteComplaint(
            @RequestParam Long houseId,
            @RequestParam Long complaintId){
        service.deleteComplaint(houseId, complaintId);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

    // 입주민 민원 수정
    @PutMapping("/update")
    @Operation(summary = "관리자 답변 전 민원 수정")
    public ResponseEntity<?> updateComplaint(
            @RequestParam Long houseId,
            @RequestParam Long complaintId,
            @RequestBody ComplaintUserUpdate complaintUserUpdate
    ){
        service.updateComplaint(houseId, complaintId, complaintUserUpdate);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }
}

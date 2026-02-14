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
    public ResponseEntity<?> getUserComplaintList(@AuthenticationPrincipal AccountUserDetail userDetail) {

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
            @PathVariable Long complaintId,
            @AuthenticationPrincipal AccountUserDetail userDetail
            ){

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
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> writeComplaint(
            @AuthenticationPrincipal AccountUserDetail userDetail,
            @RequestBody ComplaintUserWrite userWrite){

        service.write(userDetail, userWrite);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

    // 입주민 민원 삭제
    @DeleteMapping("/delete")
    @Operation(summary = "관리자 답변 전 민원 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteComplaint(
            @RequestParam Long complaintId,
            @AuthenticationPrincipal AccountUserDetail userDetail){

        String email = userDetail.getHouseholderEmail();
        Long houseId = userDetail.getAccount().getHouse().getHouseId();

        service.deleteByComplaintId(complaintId,houseId, email);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

    // 입주민 민원 수정
    @PutMapping("/update")
    @Operation(summary = "관리자 답변 전 민원 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateComplaint(
            @RequestParam Long complaintId,
            @RequestBody ComplaintUserUpdate complaintUserUpdate,
            @AuthenticationPrincipal AccountUserDetail userDetail
    ){
        String email = userDetail.getHouseholderEmail();
        Long houseId = userDetail.getAccount().getHouse().getHouseId();

        service.updateComplaint(houseId, complaintId, email,complaintUserUpdate);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }
}

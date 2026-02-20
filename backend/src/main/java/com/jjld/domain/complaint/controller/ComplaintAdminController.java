package com.jjld.domain.complaint.controller;

import com.jjld.domain.complaint.dto.admin.ComplaintAdminAnswerResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminDetailResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintSearchCond;
import com.jjld.domain.complaint.dto.user.ComplaintReference;
import com.jjld.domain.complaint.dto.user.ComplaintUserDetailResponse;
import com.jjld.domain.complaint.dto.user.ComplaintUserResponse;
import com.jjld.domain.complaint.service.ComplaintAdminService;
import com.jjld.domain.complaint.service.ComplaintAdminServiceImpl;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/complaint/api")
@RequiredArgsConstructor
public class ComplaintAdminController {

    private final ComplaintAdminServiceImpl service;

    // 관리자 민원 목록 출력
    @GetMapping("/list")
    @Operation(summary = "관리자 민원 목록 조회")
    public Map<String, Object> search(
            ComplaintSearchCond cond,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<ComplaintAdminResponse> result = service.search(cond, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("totalPages", result.getTotalPages());
        response.put("totalElements", result.getTotalElements());
        response.put("page", result.getNumber());
        return response;
    }

    // 관리자 민원 상세 조회
    @GetMapping("/detail/{complaintId}")
    @Operation(summary = "관리자 민원 상세 조회")
    public ResponseEntity<?>  getComplaint(@PathVariable Long complaintId){
        ComplaintAdminDetailResponse complaint = service.findByComplaintId(complaintId);

        return ResponseEntity.ok(
                ApiResponse.success(complaint)
        );
    }

    // 관리자 민원 답변
    @PostMapping("/write")
    @Operation(summary = "관리자 민원 답변")
    public ResponseEntity<?> writeComplaint(
            @RequestParam Long complaintId,
            @RequestParam Long adminId,
            @RequestBody ComplaintAdminAnswerResponse answerResponse
            ){
        service.answerWrite(complaintId, adminId, answerResponse);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

}

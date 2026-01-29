package com.jjld.domain.complaint.controller;

import com.jjld.domain.complaint.dto.ComplaintAdminDetailResponse;
import com.jjld.domain.complaint.dto.ComplaintAdminResponse;
import com.jjld.domain.complaint.dto.ComplaintUserDetailResponse;
import com.jjld.domain.complaint.dto.ComplaintUserResponse;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.service.ComplaintService;
import com.jjld.domain.complaint.service.ComplaintServiceImpl;
import com.jjld.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaint/api")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintServiceImpl service;
    private final ComplaintService complaintService;

    // 관리자 민원 목록 출력
    @GetMapping("/complaints")
    public Page<ComplaintAdminResponse> getComplaintList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return service.findAll(page, size);
    }

    // 관리자 민원 상세 조회
    @GetMapping("/comlaint/{complaintId}")
    public ResponseEntity<?>  getComplaint(@RequestParam("complaintId") Long complaintId){
        ComplaintAdminDetailResponse complaint = complaintService.findByComplaintId(complaintId);

        return ResponseEntity.ok(
                ApiResponse.success(complaint)
        );
    }

    // 입주민 자신이 등록한 민원 목록 조회
    @GetMapping("/user/complaint/list")
    public ResponseEntity<?> getUserComplaintList(@RequestParam Long houseId){
        List<ComplaintUserResponse> userComplaint = complaintService.findByHouse_HouseId(houseId);

        return ResponseEntity.ok(
                ApiResponse.success(userComplaint)
        );
    }

    // 입주민 자신이 등록한 민원 상세 조회
    @GetMapping("/user/complaint/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintUserDetailResponse>> getUserComplaintDetail(
            @PathVariable Long complaintId,
            @RequestParam Long houseId
    ){
        ComplaintUserDetailResponse response =
                complaintService.findByComplaintIdAndHouse_HouseId(complaintId, houseId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }
}

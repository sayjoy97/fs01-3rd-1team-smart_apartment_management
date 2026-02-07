package com.jjld.domain.complaint.service;

import com.jjld.domain.complaint.dto.user.*;
import com.jjld.domain.house.dto.login.AccountUserDetail;

import java.util.List;

public interface ComplaintUserService {

    // 로그인한 입주민 기준의 민원 목록 조회
    List<ComplaintUserResponse> findMyComplaintDetail(AccountUserDetail userDetail);

    // 로그인한 입주민 기준의 민원 상세 조회
    ComplaintUserDetailResponse findMyComplaintDetail(
            Long complaintId,
            AccountUserDetail userDetail,
            String householderEmail
    );

    // 입주민 민원 작성 시 참조할 민원 목록 조회
    List<ComplaintReference> getReferenceComplaints(Long houseId);

    // 입주민 민원 작성
    Long write(AccountUserDetail userDetail, ComplaintUserWrite userWrite);

    // 입주민 민원 삭제
    void deleteByComplaintId(Long complaintId, Long houseId, String householderEmail);

    // 입주민 민원 수정
    void updateComplaint(Long complaintId, Long houseId, String householderEmail, ComplaintUserUpdate complaintUserUpdate);
}

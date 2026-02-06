package com.jjld.domain.complaint.dao;

import com.jjld.domain.complaint.dto.user.ComplaintUserWrite;
import com.jjld.domain.complaint.entity.Complaint;

public interface ComplaintDAO {

    // 관리자의 민원 상세 조회
    Complaint findByComplaintId(Long complaintId);

    // 관리자의 민원 답변 작성
    void updateAnswer(Complaint complaint);

    // 입주민의 민원 상세 조회
    Complaint findByComplaintIdAndHouse_HouseIdAndHouseholderEmail(Long complaintId, Long houseId, String email);

    // 입주민 민원 삭제
    void deleteComplaint(Long complaintId);

    // 입주민 민원 수정
    void update(Complaint complaint);
}

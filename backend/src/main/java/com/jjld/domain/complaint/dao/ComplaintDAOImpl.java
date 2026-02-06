package com.jjld.domain.complaint.dao;


import com.jjld.domain.complaint.dto.user.ComplaintUserWrite;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ComplaintDAOImpl implements ComplaintDAO{
    private final ComplaintRepository complaintRepository;

    // 관리자의 민원 상세 조회
    @Override
    public Complaint findByComplaintId(Long complaintId) {
        return complaintRepository.findByComplaintId(complaintId);
    }

    // 관리자의 민원 답변 작성
    @Override
    public void updateAnswer(Complaint complaint) {
            complaintRepository.save(complaint);
    }

    // 입주민 자신이 작성한 민원 상세 조회
    @Override
    public Complaint findByComplaintIdAndHouse_HouseIdAndHouseholderEmail(Long complaintId, Long houseId, String email) {
        return complaintRepository.findByComplaintIdAndHouse_HouseIdAndHouseholderEmail(complaintId, houseId, email);
    }

    // 입주민 민원 삭제
    @Override
    public void deleteComplaint(Long complaintId) {
        complaintRepository.deleteByComplaintId(complaintId);
    }

    // 입주민 민원 수정
    @Override
    public void update(Complaint complaint) {
        complaintRepository.save(complaint);
    }


}

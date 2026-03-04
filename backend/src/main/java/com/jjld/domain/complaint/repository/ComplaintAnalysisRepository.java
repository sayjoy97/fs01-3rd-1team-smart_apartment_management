package com.jjld.domain.complaint.repository;

import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.entity.ComplaintAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComplaintAnalysisRepository extends JpaRepository<ComplaintAnalysis, Long> {

    // 민원 수정 시 기존 생성된 요약 삭제
    void deleteByComplaint_ComplaintId(Long complaintId);

    // 요약이 있는지 체크
    boolean existsByComplaint(Complaint complaint);


    // complaintId 기준으로 조회
    Optional<ComplaintAnalysis> findByComplaint_ComplaintId(Long complaintId);

}

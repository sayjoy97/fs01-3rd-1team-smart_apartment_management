package com.jjld.domain.complaint.repository;

import com.jjld.domain.complaint.entity.ComplaintReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintReplyRepository extends JpaRepository<ComplaintReply, Long> {
    // 마이페이지에서 관리자가 답변한 민원의 수를 조회
    long countByAdmin_AdminId(Long adminId);
}

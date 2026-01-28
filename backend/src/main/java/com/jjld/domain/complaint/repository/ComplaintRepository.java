package com.jjld.domain.complaint.repository;

import com.jjld.domain.complaint.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Complaint findByComplaintId(Long complaintId);
}

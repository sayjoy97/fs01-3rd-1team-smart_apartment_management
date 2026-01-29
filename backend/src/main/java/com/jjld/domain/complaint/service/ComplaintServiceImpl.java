package com.jjld.domain.complaint.service;

import com.jjld.domain.complaint.dao.ComplaintDAO;
import com.jjld.domain.complaint.dao.ComplaintDAOImpl;
import com.jjld.domain.complaint.dto.ComplaintAdminDetailResponse;
import com.jjld.domain.complaint.dto.ComplaintAdminResponse;
import com.jjld.domain.complaint.dto.ComplaintUserDetailResponse;
import com.jjld.domain.complaint.dto.ComplaintUserResponse;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.entity.ComplaintAnalysis;
import com.jjld.domain.complaint.entity.ComplaintReply;
import com.jjld.domain.complaint.entity.Enum.ComplaintStatus;
import com.jjld.domain.complaint.repository.ComplaintRepository;
import com.jjld.global.exception.complaint.ComplaintNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintDAOImpl complaintDAO;

    // 관리자 민원 목록 페이징으로 조회
    public Page<ComplaintAdminResponse> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("complaintId").descending());

        Page<Complaint> complaintPage = complaintRepository.findAll(pageable);
        if(complaintPage == null){
            throw new ComplaintNotFoundException("해당 페이지의 민원이 없습니다");
        }

        return complaintPage.map(ComplaintAdminResponse::new);
    }


    @Override
    public ComplaintAdminDetailResponse findByComplaintId(Long complaintId) {
        Complaint complaint = complaintDAO.findByComplaintId(complaintId);
        if (complaint == null){
            throw new ComplaintNotFoundException();
        }

        String summary = Optional.ofNullable(complaint.getComplaintAnalysis())
                .map(ComplaintAnalysis::getSummary)
                .orElse(null);

        String answer = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAnswer())
                .orElse(null);

        String admin = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAdmin().getAdminName())
                .orElse(null);

        ComplaintAdminDetailResponse adminDetailResponse = ComplaintAdminDetailResponse.builder()
                .complaintId(complaint.getComplaintId())
                .houseDong(complaint.getHouse().getHouseDong())
                .houseHo(complaint.getHouse().getHouseHo())
                .category(complaint.getCategory().name())
                .createAt(complaint.getCreatedAt())
                .updateAt(complaint.getUpdatedAt())
                .title(complaint.getTitle())
                .content(complaint.getContent())
                .summary(summary)
                .answer(answer)
                .adminName(admin)
                .build();

        return adminDetailResponse;
    }

    @Override
    public List<ComplaintUserResponse> findByHouse_HouseId(Long houseId) {
        List<Complaint> userComplaint = complaintRepository.findByHouse_HouseId(houseId);
        if(userComplaint.isEmpty()){
            throw new ComplaintNotFoundException("작성한 민원이 없습니다");
        }

        return userComplaint.stream()
                .map(complaint -> ComplaintUserResponse.builder()
                        .complaintId(complaint.getComplaintId())
                        .title(complaint.getTitle())
                        .category(String.valueOf(complaint.getCategory()))
                        .status(String.valueOf(complaint.getStatus()))
                        .createAt(complaint.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintUserDetailResponse  findByComplaintIdAndHouse_HouseId(Long houseId, Long complaintId) {
        Complaint complaint = complaintDAO.findByHouseIdComplaintId(houseId, complaintId);
        if (complaint == null){
            throw new ComplaintNotFoundException("상세 조회하려는 민원글이 없습니다");
        }

        String answer = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAnswer())
                .orElse(null);

        ComplaintUserDetailResponse userDetailResponse = ComplaintUserDetailResponse.builder()
                .complaintId(complaint.getComplaintId())
                .category(complaint.getCategory().name())
                .title(complaint.getTitle())
                .createAt(complaint.getCreatedAt())
                .updateAt(complaint.getUpdatedAt())
                .content(complaint.getContent())
                .answer(answer)
                .canEdit(complaint.getStatus() == ComplaintStatus.WAITING)
                .canDelete(complaint.getStatus() == ComplaintStatus.WAITING)
                .build();

        return userDetailResponse;
    }
}

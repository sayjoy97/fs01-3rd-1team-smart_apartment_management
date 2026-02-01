package com.jjld.domain.complaint.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.complaint.dao.ComplaintDAO;
import com.jjld.domain.complaint.dao.ComplaintDAOImpl;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminAnswerResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminDetailResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminResponse;
import com.jjld.domain.complaint.dto.user.ComplaintReference;
import com.jjld.domain.complaint.dto.user.ComplaintUserDetailResponse;
import com.jjld.domain.complaint.dto.user.ComplaintUserResponse;
import com.jjld.domain.complaint.dto.user.ComplaintUserWrite;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.entity.ComplaintAnalysis;
import com.jjld.domain.complaint.entity.ComplaintReply;
import com.jjld.domain.complaint.entity.Enum.ComplaintStatus;
import com.jjld.domain.complaint.repository.ComplaintRepository;
import com.jjld.global.exception.admin.AdminNotFoundException;
import com.jjld.global.exception.complaint.ComplaintAlreadyAnswer;
import com.jjld.global.exception.complaint.ComplaintNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintAdminServiceImpl implements ComplaintAdminService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintDAO complaintDAO;
    private final AdminDAO adminDAO;

    // 관리자 민원 목록 페이징으로 조회
    public Page<ComplaintAdminResponse> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("complaintId").descending());

        Page<Complaint> complaintPage = complaintRepository.findAll(pageable);
        if(complaintPage == null){
            throw new ComplaintNotFoundException("해당 페이지의 민원이 없습니다");
        }

        return complaintPage.map(ComplaintAdminResponse::new);
    }


    // 관리자 민원 상세 조회
    @Override
    public ComplaintAdminDetailResponse findByComplaintId(Long complaintId) {
        Complaint complaint = complaintDAO.findByComplaintId(complaintId);
        if (complaint == null){
            throw new ComplaintNotFoundException("상세하려는 민원글이 없습니다");
        }

        // ai 요약이 없을 때 null
        String summary = Optional.ofNullable(complaint.getComplaintAnalysis())
                .map(ComplaintAnalysis::getSummary)
                .orElse(null);

        // 관리자 답변이 없을 때 null
        String answer = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAnswer())
                .orElse(null);

        // 관리자 답변이 없을 때 -> 답변 작성한 관리자 ID가 null
        String admin = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAdmin().getAdminName())
                .orElse(null);

        ComplaintAdminDetailResponse adminDetailResponse = ComplaintAdminDetailResponse.builder()
                .complaintId(complaint.getComplaintId())
                .houseDong(complaint.getHouse().getHouseDong())
                .houseHo(complaint.getHouse().getHouseHo())
                .category(complaint.getCategory().name())
                .createAt(complaint.getCreatedAt())
                .replyAt(complaint.getUpdatedAt())
                .title(complaint.getTitle())
                .content(complaint.getContent())
                .summary(summary)
                .answer(answer)
                .adminName(admin)
                .build();

        return adminDetailResponse;
    }

    // 관리자 민원 답변 작성
    @Override
    public void answerWrite(Long complaintId, Long adminId, ComplaintAdminAnswerResponse answerResponse) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId);
        if(complaint==null){
            throw new ComplaintNotFoundException("답변 작성할 민원글이 없습니다.");
        }

        ComplaintReply complaintReply = complaint.getComplaintReply();

        if(complaintReply != null && complaintReply.getAnswer() != null){
            throw new ComplaintAlreadyAnswer("이미 답변이 있는 민원글 입니다");
        }

        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());
        if(admin == null){
            throw new AdminNotFoundException("존재하지 않는 관리자 번호입니다");
        }

        complaintReply = new ComplaintReply();
        complaintReply.setComplaint(complaint);
        complaint.setComplaintReply(complaintReply);
        complaintReply.setAdmin(admin);
        complaintReply.setAnswer(answerResponse.getAnswer());
        complaint.setStatus(ComplaintStatus.ANSWERED);

        complaintDAO.updateAnswer(complaint);
    }

}

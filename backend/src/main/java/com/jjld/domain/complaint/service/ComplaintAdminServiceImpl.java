package com.jjld.domain.complaint.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.complaint.ai.AiSummaryService;
import com.jjld.domain.complaint.ai.SummaryResponse;
import com.jjld.domain.complaint.dao.ComplaintDAO;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminAnswerResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminDetailResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintAdminResponse;
import com.jjld.domain.complaint.dto.admin.ComplaintSearchCond;
import com.jjld.domain.complaint.dto.user.ComplaintReferenceResponse;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.entity.ComplaintAnalysis;
import com.jjld.domain.complaint.entity.ComplaintReply;
import com.jjld.domain.complaint.entity.Enum.ComplaintStatus;
import com.jjld.domain.complaint.entity.Enum.SummaryStatus;
import com.jjld.domain.complaint.repository.ComplaintAnalysisRepository;
import com.jjld.domain.complaint.repository.ComplaintRepository;
import com.jjld.domain.complaint.specification.ComplaintSpecification;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintAdminServiceImpl implements ComplaintAdminService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintDAO complaintDAO;
    private final AdminDAO adminDAO;
    private final AiSummaryService aiSummaryService;
    private final ComplaintAnalysisRepository complaintAnalysisRepository;

    // 관리자 민원 목록 페이징으로 조회
    public Page<ComplaintAdminResponse> search(ComplaintSearchCond cond, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("complaintId").descending());

        Specification<Complaint> spec = Specification.allOf(
                ComplaintSpecification.equalCategory(cond.getCategory()),
                ComplaintSpecification.equalStatus(cond.getStatus())
        );

        return complaintRepository
                .findAll(spec, pageable)
                .map(ComplaintAdminResponse::new);
    }


    // 관리자 민원 상세 조회
    @Override
    public ComplaintAdminDetailResponse findByComplaintId(Long complaintId) {
        Complaint complaint = complaintDAO.findByComplaintId(complaintId);
        if (complaint == null){
            throw new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "상세하려는 민원글이 없습니다");
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

        // 참조 민원
        List<ComplaintReferenceResponse> refs = complaint.getReferenceComplaints()
                .stream()
                .map(ref -> new ComplaintReferenceResponse(
                        ref.getComplaintId(),
                        ref.getTitle(),
                        ref.getCategory().name(),
                        ref.getContent()
                        ))
                .collect(Collectors.toList());

        ComplaintAdminDetailResponse adminDetailResponse = ComplaintAdminDetailResponse.builder()
                .complaintId(complaint.getComplaintId())
                .houseDong(complaint.getHouse().getHouseDong())
                .houseHo(complaint.getHouse().getHouseHo())
                .houseHolderEmail(complaint.getHouseholderEmail())
                .category(complaint.getCategory().name())
                .createAt(complaint.getCreatedAt())
                .replyAt(complaint.getUpdatedAt())
                .title(complaint.getTitle())
                .content(complaint.getContent())
                .summaryStatus(complaint.getSummaryStatus().name())
                .summary(summary)
                .referencedComplaints(refs)
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
            throw new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "답변 작성할 민원글이 없습니다.");
        }

        ComplaintReply complaintReply = complaint.getComplaintReply();

        if(complaintReply != null && complaintReply.getAnswer() != null){
            throw new BadRequestException(ErrorCode.COMPLAINT_ALREADY_ANSWER, "이미 답변이 있는 민원글 입니다");
        }

        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "관리자를 찾을 수 없습니다."));
        if(admin == null){
            throw new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "존재하지 않는 관리자 번호입니다");
        }

        complaintReply = new ComplaintReply();
        complaintReply.setComplaint(complaint);
        complaint.setComplaintReply(complaintReply);
        complaintReply.setAdmin(admin);
        complaintReply.setAnswer(answerResponse.getAnswer());
        complaint.setStatus(ComplaintStatus.ANSWERED);

        complaintDAO.updateAnswer(complaint);
    }

    // 관리자 조회용 민원 요약 저장
    @Override
    public void runSummaryBatch() {
        List<Complaint> target =
                complaintRepository.findBySummaryStatus(SummaryStatus.WAITING);

        for (Complaint complaint : target) {
            try {
                String content = complaint.getContent();

                if (content.length() < 100) {
                    complaint.setSummaryStatus(SummaryStatus.NOT_REQUIRED);
                    continue;
                }

                // AI 요약 요청
                SummaryResponse res = aiSummaryService.summarize(content);

                // 기존 요약 삭제 (민원 수정됐을 경우)
                ComplaintAnalysis analysis = ComplaintAnalysis.builder()
                        .complaint(complaint)
                        .summary(res.getSummary())
                        .analysis(null)
                        .build();

                complaintAnalysisRepository.save(analysis);

                complaint.setSummaryStatus(SummaryStatus.COMPLETED);
            } catch (Exception e) {
                complaint.setSummaryStatus(SummaryStatus.FAILED);
            }
        }
    }

    // 관리자의 민원 요약 요청
    @Override
    public void generateSummary(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "민원을 찾을 수 없습니다."));

        String content = complaint.getContent();

        if (content.length() < 100) {
            complaint.setSummaryStatus(SummaryStatus.NOT_REQUIRED);
            complaintRepository.save(complaint);
            return;
        }

        try {
            // AI 요약 요청
            SummaryResponse res = aiSummaryService.summarize(content);

            Optional<ComplaintAnalysis> existing = complaintAnalysisRepository.findByComplaint_ComplaintId(complaintId);

            ComplaintAnalysis analysis = existing.orElseGet(() -> ComplaintAnalysis.builder()
                    .complaint(complaint)
                    .build());

            analysis.setSummary(res.getSummary());

            complaintAnalysisRepository.save(analysis);

            complaint.setComplaintAnalysis(analysis);
            complaint.setSummaryStatus(SummaryStatus.COMPLETED);
            complaintRepository.save(complaint);

        } catch (Exception e) {
            complaint.setSummaryStatus(SummaryStatus.FAILED);
            complaintRepository.save(complaint);
            throw new RuntimeException("요약 생성 실패", e);
        }
    }

}
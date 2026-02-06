package com.jjld.domain.complaint.service;

import com.jjld.domain.complaint.dao.ComplaintDAO;
import com.jjld.domain.complaint.dto.user.*;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.entity.Enum.ComplaintCategory;
import com.jjld.domain.complaint.entity.Enum.ComplaintStatus;
import com.jjld.domain.complaint.repository.ComplaintRepository;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.AccountRepository;
import com.jjld.domain.house.repository.HouseRepository;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintUserServiceImpl implements ComplaintUserService{
    private final ComplaintRepository complaintRepository;
    private final ComplaintDAO complaintDAO;
    private final ModelMapper modelMapper;
    private final HouseRepository houseRepository;
    private final AccountRepository accountRepository;


    // 세대별 작성한 민원 목록 조회
    @Override
    public List<ComplaintUserResponse> findMyComplaintDetail(AccountUserDetail userDetail) {
        String email = userDetail.getHouseholderEmail();
        Long houseId = userDetail.getAccount().getHouse().getHouseId();

        List<Complaint> userComplaint = complaintRepository.findByHouse_HouseIdAndHouseholderEmail(houseId, email);
        if(userComplaint.isEmpty()){
            throw new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "작성한 민원이 없습니다");
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

    // 자신이 작성한 민원 상세 조회
    @Override
    public ComplaintUserDetailResponse findMyComplaintDetail(Long complaintId, AccountUserDetail userDetail, String householderEmail) {
        String email = userDetail.getUsername();

        log.info("JWT에서 가져온 이메일: {}", email);

        Account account = accountRepository.findByHouseholderEmail(email);
        if (account == null){
            throw new NotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND,"계정이 없습니다.");
        }

        Long houseId = Optional.ofNullable(account.getHouse())
                .map(House::getHouseId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "세대 정보가 존재하지 않습니다."));
        log.info("JWT에서 가져온 houseId: {}", houseId);


        Complaint complaint = complaintDAO.findByComplaintIdAndHouse_HouseIdAndHouseholderEmail(complaintId, houseId, email);
        if (complaint == null){
            throw new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "상세 조회하려는 민원글이 없습니다");
        }

        // 관리자 답변이 없을 때 -> 답변 작성한 관리자 ID가 null
        String admin = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAdmin().getAdminName())
                .orElse(null);

        String answer = Optional.ofNullable(complaint.getComplaintReply())
                .map(r -> r.getAnswer())
                .orElse(null);

        ComplaintUserDetailResponse userDetailResponse = ComplaintUserDetailResponse.builder()
                .complaintId(complaint.getComplaintId())
                .householderEmail(userDetail.getHouseholderEmail())
                .category(complaint.getCategory().name())
                .title(complaint.getTitle())
                .createAt(complaint.getCreatedAt())
                .replyAt(complaint.getUpdatedAt())
                .content(complaint.getContent())
                .answer(answer)
                .adminName(admin)
                .canEdit(complaint.getStatus() == ComplaintStatus.WAITING)
                .canDelete(complaint.getStatus() == ComplaintStatus.WAITING)
                .build();

        return userDetailResponse;
    }

    // 민원 작성 시 참조할 민원 목록
    @Override
    public List<ComplaintReference> getReferenceComplaints(Long houseId) {
        List<Complaint> reference = complaintRepository.findByHouse_HouseIdOrderByCreatedAtDesc(houseId);
        if (reference.isEmpty()) {
            throw new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "참조할 민원 내역이 없습니다");
        }
        return reference.stream()
                .map(r -> new ComplaintReference(
                        r.getComplaintId(),
                        r.getTitle(),
                        r.getCategory().name(),
                        r.getCreatedAt()
                )).toList();
    }

    // 입주민 민원 작성
    @Override
    public Long write(Long houseId, ComplaintUserWrite userWrite) {
        House house = houseRepository.findByHouseId(houseId);
        if(house == null){
            throw new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "없는 세대 번호입니다");
        }

        // 참조할 민원이 없으면 빈 리스트 처리, 있으면 엔티티 리스트로 변환
        List<Complaint> reference = Optional.ofNullable(userWrite.getReferenceId())
                .orElse(Collections.emptyList())
                .stream()
                .map(refId -> complaintRepository.findById(refId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "참조할 민원이 없습니다.")))
                .collect(Collectors.toList());

        Complaint complaint = Complaint.builder()
                .title(userWrite.getTitle())
                .category(ComplaintCategory.valueOf((userWrite.getCategory())))
                .content(userWrite.getContent())
                .referenceComplaints(reference)
                .status(ComplaintStatus.WAITING)
                .house(house)
                .build();

        Complaint save = complaintRepository.save(complaint);

        return save.getComplaintId();

    }

    // 민원 삭제
    @Override
    public void deleteComplaint(Long houseId, Long complaintId) {

        Complaint complaint = complaintRepository
                .findByHouse_HouseIdAndComplaintId(houseId, complaintId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "삭제하려는 민원글을 찾을 수 없습니다."));

        if (complaint.getComplaintReply() != null) {
            throw new NotFoundException(ErrorCode.COMPLAINT_ALREADY_ANSWER, "답변이 달린 민원은 삭제할 수 없습니다");
        }

        // 삭제 대상 complaint 참조하는 자식 complaint를 찾아서 삭제
        List<Complaint> referenceComplaint = complaintRepository.findAllByReferenceComplaintsContains(complaint);
        for(Complaint ref : referenceComplaint){
            ref.getReferenceComplaints().remove(complaint);
        }
        // reference 초기화
        complaint.getReferenceComplaints().clear();

        complaintRepository.deleteByComplaintId(complaintId);
    }

    // 민원 수정
    @Override
    public void updateComplaint(Long houseId, Long complaintId, ComplaintUserUpdate complaintUserUpdate) {
    Complaint complaint = complaintRepository
            .findByHouse_HouseIdAndComplaintId(houseId, complaintId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.COMPLAINT_NOT_FOUND, "수정하려는 민원글을 찾을 수 없습니다."));

    if(complaint.getComplaintReply() != null){
        throw new NotFoundException(ErrorCode.COMPLAINT_ALREADY_ANSWER, "답변이 달린 민원은 수정할 수 없습니다");
    }
    complaint.setTitle(complaintUserUpdate.getTitle());
    complaint.setCategory(ComplaintCategory.valueOf(complaintUserUpdate.getCategory()));
    complaint.setContent(complaintUserUpdate.getContent());
    complaint.setUpdatedAt(LocalDateTime.now());

    if(complaintUserUpdate.getReferenceId() != null){
        List<Complaint> references =
                complaintRepository.findAllById(complaintUserUpdate.getReferenceId());

        complaint.setReferenceComplaints(references);
    }

    complaintDAO.update(complaint);
    }
}

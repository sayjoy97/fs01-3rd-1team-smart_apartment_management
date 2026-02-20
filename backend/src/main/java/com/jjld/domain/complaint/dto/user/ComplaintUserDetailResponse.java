package com.jjld.domain.complaint.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintUserDetailResponse {
    private Long complaintId;
    private String householderEmail;
    private String category;
    private String title;
    private LocalDateTime createAt;
    private LocalDateTime replyAt;
    private String content;
    private String answer;
    private String adminName;

    private boolean canEdit;
    private boolean canDelete;

    private List<ComplaintReferenceResponse> referencedComplaints;

}

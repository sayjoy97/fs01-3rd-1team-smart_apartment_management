package com.jjld.domain.complaint.dto.admin;

import com.jjld.domain.complaint.dto.user.ComplaintReferenceResponse;
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
public class ComplaintAdminDetailResponse {
    private Long complaintId;
    private String title;
    private String category;
    private Integer houseDong;
    private Integer houseHo;
    private String houseHolderEmail;
    private LocalDateTime createAt;
    private LocalDateTime replyAt;
    private String content;
    private String summaryStatus;

    private String summary;
    private String answer;
    private String adminName;

    private List<ComplaintReferenceResponse> referencedComplaints;

}

package com.jjld.domain.complaint.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintReferenceResponse {
    private Long complaintId;
    private String title;
    private String category;
    private String content;
}

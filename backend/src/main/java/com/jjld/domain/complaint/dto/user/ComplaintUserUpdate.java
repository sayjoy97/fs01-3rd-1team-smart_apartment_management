package com.jjld.domain.complaint.dto.user;

import com.jjld.domain.complaint.entity.Complaint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintUserUpdate {
    private String title;
    private String category;
    private String content;

    private List<Long> referenceId;

    public List<Long> getReferenceId() {
        return referenceId;
    }
}

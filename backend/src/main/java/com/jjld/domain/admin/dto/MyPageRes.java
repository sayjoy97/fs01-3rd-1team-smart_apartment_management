package com.jjld.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageRes {
    AdminRes adminRes;
    long resolvedComplaintCount;
    long postedNoticeCount;
    long totalWorkingDays;
}

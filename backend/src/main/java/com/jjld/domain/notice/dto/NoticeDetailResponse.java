package com.jjld.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailResponse {
    private Long noticeId;
    private String adminName;
    private String noticeTitle;
    private String noticeContent;
    private Boolean fixStatus;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

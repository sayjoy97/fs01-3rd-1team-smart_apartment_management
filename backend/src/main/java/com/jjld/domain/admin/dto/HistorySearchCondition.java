package com.jjld.domain.admin.dto;

import com.jjld.domain.admin.entity.Enum.AccessType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorySearchCondition {
    private AccessType accessType;  // 접근 타입
    private Boolean success;  // 성공 여부
}

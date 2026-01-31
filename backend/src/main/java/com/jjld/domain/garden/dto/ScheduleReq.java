package com.jjld.domain.garden.dto;

import com.jjld.domain.garden.entity.Enum.Priority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleReq {@NotNull(message = "관리자 설정은 필수입니다.")
    private Long adminId;

    @NotBlank(message = "제목 입력은 필수입니다.")
    @Size(max = 500, message = "제목은 500자 이하여야 합니다")
    private String workTitle;

    private String workContent;

    @NotNull(message = "시작일 설정은 필수입니다.")
    @FutureOrPresent(message = "현재 이후 날짜만 가능합니다.")
    private LocalDate workStartDate;

    @NotNull(message = "종료일 설정은 필수입니다.")
    @FutureOrPresent(message = "현재 이후 날짜만 가능합니다.")
    private LocalDate workEndDate;

    @NotNull(message = "중요도 설정은 필수입니다.")
    private Priority priority;
}

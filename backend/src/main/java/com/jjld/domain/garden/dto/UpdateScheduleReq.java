package com.jjld.domain.garden.dto;

import com.jjld.domain.garden.entity.Enum.Priority;
import com.jjld.domain.garden.entity.Enum.ScheduleState;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleReq {
    @NotNull(message = "관리자 설정은 필수입니다.")
    private Long adminId;

    @NotBlank(message = "제목 입력은 필수입니다.")
    @Size(max = 500, message = "제목은 500자 이하여야 합니다")
    private String workTitle;

    private String workContent;

    @NotNull(message = "시작일 설정은 필수입니다.")
    private LocalDate workStartDate;

    @NotNull(message = "종료일 설정은 필수입니다.")
    private LocalDate workEndDate;

    @NotNull(message = "진행 상태 설정은 필수입니다.")
    private ScheduleState state;

    @NotNull(message = "중요도 설정은 필수입니다.")
    private Priority priority;


}

package com.jjld.domain.garden.dto;

import com.jjld.domain.garden.entity.Enum.DeviceState;
import com.jjld.domain.garden.entity.Enum.DeviceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReq {
    @NotNull(message = "타입 설정은 필수입니다.")
    private DeviceType deviceType;

    @NotNull(message = "상태 설정은 필수입니다.")
    private DeviceState state;
}

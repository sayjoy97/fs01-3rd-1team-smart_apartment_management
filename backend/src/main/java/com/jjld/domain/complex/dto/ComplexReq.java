package com.jjld.domain.complex.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplexReq {
    @NotBlank(message = "단지명 설정은 필수입니다.")
    private String name;

    @NotBlank(message = "총 세대수 설정은 필수입니다.")
    private int totalHouseholds;

    @NotBlank(message = "주소 설정은 필수입니다.")
    private String address;

    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    private String phoneNumber;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "준공일 설정은 필수입니다.")
    private LocalDate completionDate;

    @NotBlank(message = "동 수 설정은 필수입니다.")
    private int buildingCount;

    @NotBlank(message = "최고 층 수 설정은 필수입니다.")
    private int maxFloor;

    private Integer parkingCapacity;
}

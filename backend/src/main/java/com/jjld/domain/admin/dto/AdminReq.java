package com.jjld.domain.admin.dto;

import com.jjld.domain.admin.entity.Enum.AdminRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 관리자 추가 시 요청 받을 DTO

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReq {
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, message = "아이디는 4글자 이상이어야 합니다.")
    private String adminLoginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8글자 이상이어야 합니다.")
    private String adminPass;

    @NotBlank(message = "이름은 필수입니다.")
    private String adminName;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    private String adminPhone;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String adminEmail;

    @NotNull(message = "권한 설정은 필수입니다.")
    private AdminRole adminRole;
}

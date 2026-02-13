package com.jjld.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dao.HistoryDAO;
import com.jjld.domain.admin.dto.LoginAdminRes;
import com.jjld.domain.admin.dto.LoginRes;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AccessType;
import com.jjld.domain.admin.entity.History;
import com.jjld.domain.admin.security.AdminUserDetail;
import com.jjld.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider tokenProvider;
    private final AdminDAO adminDAO;
    private final HistoryDAO historyDAO;
    private final ModelMapper modelMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException {
        AdminUserDetail adminUserDetail = (AdminUserDetail) authentication.getPrincipal();
        Admin admin = adminUserDetail.getAdmin();

        // ✅ JWT 생성
        String jwtToken = tokenProvider.createToken(authentication);

        // ✅ IP 추출
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        // ✅ 로그인 성공 기록
        History history = History.builder()
                .admin(admin)
                .ipAddress(ipAddress)
                .accessType(AccessType.LOGIN)
                .success(true)
                .message("로그인 성공")
                .build();
        historyDAO.createLog(history);

        // ✅ 관리자 상태 변경
        admin.setState(true);
        adminDAO.updateAdmin(admin);

        // ✅ 응답 DTO 생성
        LoginAdminRes loginAdminRes = modelMapper.map(admin, LoginAdminRes.class);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        LoginRes loginRes = LoginRes.builder()
                .accessToken(jwtToken)
                .username(adminUserDetail.getUsername())
                .roles(roles)
                .loginAdminRes(loginAdminRes)
                .build();

        // ✅ JSON 응답으로 내려주기
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), ResponseEntity.ok(ApiResponse.success(loginRes)));
    }
}



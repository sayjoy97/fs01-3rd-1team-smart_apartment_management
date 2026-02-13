package com.jjld.global.security;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dao.HistoryDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AccessType;
import com.jjld.domain.admin.entity.History;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final AdminDAO adminDAO;
    private final HistoryDAO historyDAO;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception
    ) throws IOException {
        String loginId = request.getParameter("adminLoginId");

        Admin admin = adminDAO.findByAdminLoginId(loginId).orElse(null);

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        if (exception instanceof BadCredentialsException && admin != null) {
            History history = History
                    .builder()
                    .admin(admin)
                    .ipAddress(ipAddress)
                    .accessType(AccessType.LOGIN)
                    .success(false)
                    .message("아이디 또는 비밀번호가 일치하지 않습니다.")
                    .build();
            historyDAO.createLog(history);
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);response.setContentType("application/json;charset=UTF-8");

//        Map<String, Object> body = new HashMap<>();
//        body.put("success", false);
//        body.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
//
//        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

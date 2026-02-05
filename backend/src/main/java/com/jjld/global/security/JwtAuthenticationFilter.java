package com.jjld.global.security;

import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) request;
        String jwtToken = getToken(rq);
        log.info(jwtToken);

        // 유효성 체크
        if(StringUtils.hasText(jwtToken) && tokenProvider.validatorToken(jwtToken)){
            Authentication authentication = tokenProvider.getAuthentication(jwtToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else{
            log.info("인증되지 않은 사용자로, 토큰이 없습니다.");
        }
        chain.doFilter(request, response);

    }

    // 클라이언트와 요청정보에서 토큰을 꺼내 리턴
    private String getToken(HttpServletRequest rq) {
        String bearerToken = rq.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        return null;
    }
}

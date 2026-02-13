package com.jjld.global.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class AdminLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public AdminLoginAuthenticationFilter(AuthenticationManager authenticationManager,
                                          AuthenticationFailureHandler failureHandler,
                                          AuthenticationSuccessHandler successHandler) {
        setFilterProcessesUrl("/admin/api/login");
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(failureHandler);
        setAuthenticationSuccessHandler(successHandler);
    }
}

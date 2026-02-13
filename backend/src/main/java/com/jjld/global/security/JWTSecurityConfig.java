package com.jjld.global.security;

import com.jjld.domain.admin.security.AdminAuthenticationProvider;
import com.jjld.domain.house.service.AccountAuthenticationProvider;
import com.jjld.domain.house.service.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
public class JWTSecurityConfig {

    private final AccountAuthenticationProvider accountAuthenticationProvider;
    private final AdminAuthenticationProvider adminAuthenticationProvider;
    private final JwtTokenProvider tokenProvider;
    private final AccountDetailsService accountDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(accountAuthenticationProvider)
                   .authenticationProvider(adminAuthenticationProvider);
        return authBuilder.build();
    }

    @Bean
    public AuthenticationManager adminAuthenticationManager(
            AdminAuthenticationProvider adminAuthenticationProvider) {

        return new ProviderManager(adminAuthenticationProvider);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminLoginFilterChain(
            HttpSecurity http,
            AuthenticationManager adminAuthenticationManager
    ) throws Exception {
        AdminLoginAuthenticationFilter adminLoginFilter =
                new AdminLoginAuthenticationFilter(
//                        http.getSharedObject(AuthenticationManager.class),
                        adminAuthenticationManager,
                        customAuthenticationFailureHandler,
                        customLoginSuccessHandler
                );
//        adminLoginFilter.setFilterProcessesUrl("/admin/api/login");

        http
//                .securityMatcher("/admin/api/login")
                .securityMatcher("/admin/api/login", "POST")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterAt(adminLoginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/account/api/login", "/admin/api/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin/api", "/elevator/api/admin/{adminId}", "/complex/api/{adminId}").hasAnyRole("SUPER_ADMIN", "ACTING_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/api/{adminId}/authority/{targetAdminId}", "/complex/api/{adminId}").hasAnyRole("SUPER_ADMIN", "ACTING_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/api/{adminId}", "/elevator/api/{elevatorId}/admin/{adminId}", "/garden/api/{gardenId}/admin/{adminId}").hasAnyRole("SUPER_ADMIN", "ACTING_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, accountDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

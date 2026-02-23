package com.jjld.global.security;

import com.jjld.domain.house.service.AccountAuthenticationProvider;
import com.jjld.domain.house.service.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
public class JWTSecurityConfig {

    private final AccountAuthenticationProvider accountAuthenticationProvider;
    private final JwtTokenProvider tokenProvider;
    private final AccountDetailsService accountDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(accountAuthenticationProvider);
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/account/api/login","/house/api/**","/complaint/api/**","/entrance/api/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, accountDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // CORS오류 처리
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configurationSource = new CorsConfiguration();

        // 허용 주소
        configurationSource.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://192.168.14.59:5173",
                "http://localhost:8081",
                "http://localhost:9600"
        ));

        configurationSource.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configurationSource.setAllowedHeaders(List.of("*"));
        configurationSource.setAllowCredentials(true);
        configurationSource.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configurationSource);
        return source;
    }

}

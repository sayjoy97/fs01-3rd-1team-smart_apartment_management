package com.jjld.global.security;


import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.dto.LoginAdminRes;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.domain.house.service.AccountDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    private final String secret;
    private final long tokenExTime;
    private final AccountDetailsService accountDetailsService;
    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.token-valid-in-second}") long tokenExTime, AccountDetailsService accountDetailsService) {
        this.secret = secret;
        this.tokenExTime = tokenExTime;
        this.accountDetailsService = accountDetailsService;
    }

    @PostConstruct
    public void init(){
        byte[] decodedata = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedata);
    }

    // 토큰 생성
    public String createToken(Authentication authentication) {
        String userId = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date now = new Date();
        Date exDate = new Date(now.getTime() + this.tokenExTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
//    public String createToken(Authentication userInfo){
//        String userId;
//        String role;
//
//        Object principal = userInfo.getPrincipal();
//
//        if(principal instanceof UserLoginRequest userLoginRequest){
//            userId = userLoginRequest.getHouseholderEmail();
//            role = "ROLE_USER";
//        }else if(principal instanceof AdminRes adminRes){
//            userId = adminRes.getAdminLoginId();
//            role = "ROLE_ADMIN";
//        }else{
//            userId = userInfo.getName();
//            role = userInfo.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .findFirst()
//                    .orElse("ROLE_USER");
//        }
//
//        // 토큰 만료 시간
//        Date now = new Date();
//        Date exDate = new Date(now.getTime()+this.tokenExTime);
//
//        String jwtToken = Jwts.builder()
//                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
//                .setSubject(userId)
//                .claim("role", role)
//                .setIssuedAt(now)
//                .setExpiration(exDate)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//        return jwtToken;
//    }

    // 토큰 유효성 검증
    public boolean validatorToken(String token){
        try{
            Jws<Claims> clamis = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;

        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 형식으로 서명된 토큰입니다.");
        }catch (ExpiredJwtException e){
            log.info("만료된 토큰입니다.");
        }catch (UnsupportedJwtException e){
            log.info("지원되지 않는 토큰");
        }
        return false;
    }

    // 인증정보를 스프링시큐리티 내부에서 인식하도록.
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getSubject();

        // roles 배열 우선, 없으면 role 단일 fallback, 그것도 없으면 ROLE_USER 기본값
        List<String> roles;
        Object rolesClaim = claims.get("roles");

        if (rolesClaim instanceof List<?> list) {
            roles = list.stream().map(Object::toString).toList();
        } else {
            String singleRole = claims.get("role", String.class);
            roles = (singleRole != null) ? List.of(singleRole) : List.of("ROLE_USER");
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .map(a -> (GrantedAuthority) a)
                .toList();

        // ROLE_USER면 기존처럼 AccountUserDetail을 principal로 올려준다 (입주민 안전)
        if (roles.contains("ROLE_USER")) {
            AccountUserDetail accountUserDetail =
                    (AccountUserDetail) accountDetailsService.loadUserByUsername(userId);
            return new UsernamePasswordAuthenticationToken(accountUserDetail, token, authorities);
        }

        // 관리자는 principal을 userId(String)로 둬도 됨
        return new UsernamePasswordAuthenticationToken(userId, token, authorities);

        // 권한 정보 추출
//        List<GrantedAuthority> authorityList =
//                Arrays.stream(claims.get("role").toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//
//        AccountUserDetail accountUserDetail = (AccountUserDetail) accountDetailsService.loadUserByUsername(claims.getSubject());
//
//        return new UsernamePasswordAuthenticationToken(accountUserDetail, token, authorityList);
//    }
    }
}

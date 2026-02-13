package com.jjld.global.security;


import com.jjld.domain.admin.dto.AdminReq;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.service.AccountDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
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
    public String createToken(Authentication userInfo){
        String userId;
        String role;

        Object principal = userInfo.getPrincipal();

        if(principal instanceof UserLoginRequest userLoginRequest){
            userId = userLoginRequest.getHouseholderEmail();
            role = "ROLE_USER";
        }else if(principal instanceof AdminReq adminReq){
            userId = adminReq.getAdminLoginId();
            role = adminReq.getAdminRole().name();
        }else{
            userId = userInfo.getName();
            role = userInfo.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");
        }

        // 토큰 만료 시간
        Date now = new Date();
        Date exDate = new Date(now.getTime()+this.tokenExTime);

        String jwtToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return jwtToken;
    }

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
    public Authentication getAuthentication(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 권한 정보 추출
        List<GrantedAuthority> authorityList =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        AccountUserDetail accountUserDetail = (AccountUserDetail) accountDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(accountUserDetail, token, authorityList);
    }
}

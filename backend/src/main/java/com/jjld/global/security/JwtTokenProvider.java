package com.jjld.global.security;


import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.dto.LoginAdminRes;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final String secret;
    private final long tokenExTime;
    private Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.token-valid-in-second}") long tokenExTime) {
        this.secret = secret;
        this.tokenExTime = tokenExTime;
    }

    @PostConstruct
    public void init(){
        byte[] decodedata = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedata);
    }

    // 토큰 생성
    public String createToken(Authentication userInfo){
        String authorityList = userInfo.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 만료 시간
        Date now = new Date();
        Date exDate = new Date(now.getTime()+this.tokenExTime);

        String userId="";
        if(userInfo.getPrincipal() instanceof UserLoginRequest userLogin){
            userId = userLogin.getHouseholderEmail();
        } else if (
                userInfo.getPrincipal() instanceof AdminRes adminRes){
            userId = adminRes.getAdminLoginId();
        }else{
            userId = userInfo.getName();
        }

        String jwtToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(userId)
                .claim("role", authorityList)
                .setIssuedAt(now)
                .setExpiration(exDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return jwtToken;

    }
}

package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.security.UserAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountAuthenticationProvider implements AuthenticationProvider {
    private final AccountDetailsService accountDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserLoginRequest request = (UserLoginRequest) authentication.getDetails();
        Integer dong = request.getHouseDong();
        Integer ho = request.getHouseHo();

        AccountUserDetail accountDetail =
                (AccountUserDetail) accountDetailsService.loadUserByUsername(username);

        if(accountDetail == null){
            throw new BadCredentialsException("계정이 존재하지 않습니다.");
        }

        if(!passwordEncoder.matches(password, accountDetail.getPassword())){

            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        if(accountDetail.getAccount().getHouse() == null ||
                !accountDetail.getAccount().getHouse().getHouseDong().equals(dong) ||
                !accountDetail.getAccount().getHouse().getHouseHo().equals(ho)){
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "아파트 동/호수가 일치하지 않습니다.");
        }
        if(!accountDetail.getAccount().isActive()){
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "비활성 계정은 로그인할 수 없습니다.");
        }


        return new UserAuthenticationToken(
                accountDetail,
                null,
                accountDetail.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

package com.jjld.domain.house.service;

import com.jjld.domain.admin.dto.TokenRes;
import com.jjld.domain.admin.repository.RefreshTokenRepository;
import com.jjld.domain.house.dto.HouseDetailResponse;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.UserHouseDetailResponse;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.entity.UserRefreshToken;
import com.jjld.domain.house.repository.AccountRepository;
import com.jjld.domain.house.repository.UserRefreshTokenRepository;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseService houseService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountDetailsService accountDetailsService;
    private final JwtTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    // 비밀번호 변경
    @Override
    public void changePassword(Long accountId, String currentPassword, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        if(!account.isFirstLogin()){
            if(currentPassword == null || !passwordEncoder.matches(currentPassword, account.getPassword())){
                throw new RuntimeException("현재 비밀번호가 틀립니다.");
            }
        }

        // 새 비밀번호
        account.setPassword(
                passwordEncoder.encode(newPassword)
        );

        account.setFirstLogin(false);

        accountRepository.save(account);
    }

    // 로그인한 유저 정보
    @Override
    public UserHouseDetailResponse getDetail(Long houseId) {
        HouseDetailResponse detail = houseService.getDetail(houseId);

        return UserHouseDetailResponse.builder()
                .houseDong(detail.getHouseDong())
                .houseHo(detail.getHouseHo())
                .householderName(detail.getHouseholderName())
                .householderPhone(detail.getHouseholderPhone())
                .householderEmail(detail.getHouseholderEmail())
                .moveInAt(detail.getMoveInAt())
                .build();
    }

    // 로그인한 유저 정보
    @Override
    public Long getMyHouse(String householderEmail) {
        House house = accountRepository.findByHouseholderEmail(householderEmail).getHouse();
        if(house == null){
            throw new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "세대정보를 찾지 못했습니다");
        }

        return house.getHouseId();
    }

    // 유저 로그인
    @Override
    @Transactional // 데이터베이스 조회가 포함되므로 추가 권장
    public UserLoginResponse login(UserLoginRequest request) {
        // 1. 이메일로 사용자 조회 (AccountRepository에 해당 메서드가 있다고 가정)
        Account account = accountRepository.findByHouseholderEmail(request.getHouseholderEmail());

        if (account == null) {
            throw new NotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        // 2. 비밀번호 직접 검증 (BCryptPasswordEncoder 활용)
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CURRENT_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }

        // 3. 동/호수 검증
        if (account.getHouse() == null ||
                !account.getHouse().getHouseDong().equals(request.getHouseDong()) ||
                !account.getHouse().getHouseHo().equals(request.getHouseHo())) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "아파트 동/호수가 일치하지 않습니다.");
        }

        // 4. 계정 활성화 여부 확인 (추가된 부분)
        if (!account.isActive()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "비활성 계정은 로그인할 수 없습니다.");
        }

        // 6. 인증 객체 생성 및 토큰 발급
        AccountUserDetail accountDetail = new AccountUserDetail(account);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                accountDetail, null, accountDetail.getAuthorities());

        // 7. 토큰 생성 로직 (기존과 동일)
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        Long accountId = account.getAccountId();

        // 기존 리프레시 토큰 삭제 및 저장
        userRefreshTokenRepository.deleteByAccountId(accountId);

        UserRefreshToken newRefreshToken = UserRefreshToken.builder()
                .accountId(accountId)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        userRefreshTokenRepository.save(newRefreshToken);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .username(accountDetail.getUsername())
                .role(accountDetail.getAuthorities().iterator().next().getAuthority())
                .firstLogin(account.isFirstLogin())

                .build();
    }

    @Override
    public TokenRes refresh(String refreshToken) {
        log.info("RefreshToken={}", refreshToken);

        UserRefreshToken savedToken =
                userRefreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() ->
                                new UnauthorizedException(ErrorCode.INVALID_TOKEN, "저장된 토큰이 없습니다."));

        if (savedToken.getExpiryDate().isBefore(LocalDateTime.now())){
            userRefreshTokenRepository.delete(savedToken);
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, "Refresh Token 만료");
        }

        AccountUserDetail userDetail=
                (AccountUserDetail) accountDetailsService
                        .loadUserByUsername(String.valueOf(savedToken.getAccountId()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetail,
                        null,
                        userDetail.getAuthorities()
                );

        String newAccessToken = tokenProvider.createToken(authentication);

        return new TokenRes(newAccessToken);
    }
}

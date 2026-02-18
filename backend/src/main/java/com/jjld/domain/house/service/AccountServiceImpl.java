package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.HouseDetailResponse;
import com.jjld.domain.house.dto.login.UserHouseDetailResponse;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.AccountRepository;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseService houseService;

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

    @Override
    public Long getMyHouse(String householderEmail) {
        House house = accountRepository.findByHouseholderEmail(householderEmail).getHouse();
        if(house == null){
            throw new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "세대정보를 찾지 못했습니다");
        }

        return house.getHouseId();
    }
}

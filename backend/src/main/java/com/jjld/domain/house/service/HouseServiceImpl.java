package com.jjld.domain.house.service;

import com.jjld.domain.house.dao.HouseDAO;
import com.jjld.domain.house.dto.HouseDetailResponse;
import com.jjld.domain.house.dto.HouseManagementResponse;
import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.dto.HouseSearchCond;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.EntranceCard;
import com.jjld.domain.house.entity.Enum.CardStatus;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.AccountRepository;
import com.jjld.domain.house.repository.EntranceCardRepository;
import com.jjld.domain.house.repository.HouseRepository;
import com.jjld.domain.house.specification.HouseSpecification;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import com.jjld.global.exception.house.CardAlreadyAssigned;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService{
    private final HouseRepository houseRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntranceCardRepository entranceCardRepository;
    private final HouseDAO houseDAO;

    // 세대 목록 조회
    @Override
    public List<HouseResponse> search(HouseSearchCond cond) {

        Specification<House> spec = Specification.allOf(
                HouseSpecification.equalHouseDong(cond.getHouseDong()),
                HouseSpecification.equalHouseHo(cond.getHouseHo()),
                HouseSpecification.equalHouseholderName(cond.getHouseholderName())
        );

        List<House> houseList = houseRepository.findAll(spec);

        return houseList.stream()
                .map(h -> new HouseResponse(
                        h.getHouseId(),
                        h.getHouseDong(),
                        h.getHouseHo(),
                        h.getHouseholderName(),
                        h.getHouseholderPhone(),
                        h.getMoveInAt(),
                        h.getHouseStatus()
                ))
                .toList();
    }

    // 세대 상세 조회
    @Override
    public HouseDetailResponse getDetail(Long houseId) {
        House house = houseDAO.findHouseId(houseId);
        if(houseId == null){
            throw new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "없는 세대 번호입니다");
        }

        return HouseDetailResponse.builder()
                .houseId(house.getHouseId())
                .houseDong(house.getHouseDong())
                .houseHo(house.getHouseHo())
                .householderName(house.getHouseholderName())
                .householderPhone(house.getHouseholderPhone())
                .householderEmail(house.getHouseholderEmail())
                .entrancePass(house.getEntrancePass())
                .moveInAt(house.getMoveInAt())
                .householdSize(house.getHouseholdSize())
                .cardUid(
                        house.getCardList().stream()
                                .map(EntranceCard::getCardUid)
                                .toList()
                )
                .build();
    }

    // 세대 등록
    @Override
    public void houseInsert(Long houseId, HouseManagementResponse houseManagementResponse) {
        // 세대 조회
        House house = houseRepository.findByHouseId(houseId);

        if(house == null){
            throw new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "존재하지 않는 세대 입니다.");
        }

        // 등록하는 세대 카드가 없으면 빈 리스트 처리
        List<EntranceCard> houseCardList = Optional.ofNullable(houseManagementResponse.getCardUid())
                .orElse(Collections.emptyList())
                .stream()
                .map(uid ->{
                        EntranceCard card = entranceCardRepository.findByCardUid(uid)
                                .orElse(null);

                        // 카드가 이미 존재하는 경우
                        if (card != null) {
                            if(card.getHouse() != null &&
                            !card.getHouse().getHouseId().equals(houseId)){
                                throw new CardAlreadyAssigned("이미 다른 세대에 등록된 카드입니다.");
                            }
                            return card;
                        }
                            // DB에 없는 카드면 생성
                            EntranceCard newCard = EntranceCard.builder()
                                    .cardUid(uid)
                                    .status(CardStatus.ACTIVE)
                                    .build();
                            return entranceCardRepository.save(newCard);
                        })
                .collect(Collectors.toList());

        // 기존 카드 리스트
        List<EntranceCard> existingCards = new ArrayList<>(house.getCardList());

        // 기존 카드 중 새 카드 리스트에 없는 카드 처리
        existingCards.forEach(card -> {
            if(!houseCardList.contains(card)){
                card.setHouse(null);   // houseId 제거로 세대 연결 끊기
                card.setStatus(CardStatus.INACTIVE);
            }
        });

        // 카드에 세대 연결
        houseCardList.forEach(card -> card.setHouse(house));
        house.setCardList(houseCardList);

        String pass = houseManagementResponse.getEntrancePass();

        house.setHouseholderName(houseManagementResponse.getHouseholderName());
        house.setHouseholderPhone(houseManagementResponse.getHouseholderPhone());
        house.setHouseholderEmail(houseManagementResponse.getHouseholderEmail());
        house.setHouseholdSize(houseManagementResponse.getHouseholdSize());
        boolean hasEmail = hasText(houseManagementResponse.getHouseholderEmail());
        boolean hasName = hasText(houseManagementResponse.getHouseholderName());
        boolean hasPhone = hasText(houseManagementResponse.getHouseholderPhone());

        // 이메일 기준 + 보조 정보
        boolean active = hasEmail && (hasName || hasPhone);

        // 최종 상태 저장
        house.setHouseStatus(active);
        if(pass == null || pass.isBlank()){
            house.setEntrancePass(pass);
        }else{
            house.setEntrancePass(passwordEncoder.encode(houseManagementResponse.getEntrancePass()));
        }

        houseRepository.save(house);

        String emailId = houseManagementResponse.getHouseholderEmail();

        if(emailId != null && !emailId.isBlank()) {
            Account account = accountRepository.findByHouseholderEmail(emailId);

            if (account == null) {
                Account newAccount = Account.builder()
                        .householderEmail(houseManagementResponse.getHouseholderEmail())
                        .password(passwordEncoder.encode(houseManagementResponse.getEntrancePass()))
                        .firstLogin(true)
                        .emailVerified(false)
                        .active(house.getHouseholderName() != null && !house.getHouseholderName().isBlank())
                        .role("ROLE_USER")
                        .house(house)
                        .build();

                accountRepository.save(newAccount);
            } else {
                account.setActive(house.getHouseholderName() != null && !house.getHouseholderName().isBlank());
                account.setHouse(house);

                if(pass != null && !pass.isBlank()){
                    account.setPassword(passwordEncoder.encode(pass));
                }

                accountRepository.save(account);
            }
        }else{
                Account accountByHouse = accountRepository.findByHouse_HouseIdAndActiveTrue(houseId);
                if(accountByHouse != null){
                    accountByHouse.setActive(false);
                    accountByHouse.setHouse(null);
                    accountRepository.save(accountByHouse);
                }

        }
        }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }


}

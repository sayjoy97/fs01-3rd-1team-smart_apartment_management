package com.jjld.domain.house.service;

import com.jjld.domain.house.dao.HouseDAO;
import com.jjld.domain.house.dto.HouseManagementResponse;
import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.dto.HouseSearchCond;
import com.jjld.domain.house.entity.EntranceCard;
import com.jjld.domain.house.entity.Enum.CardStatus;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.EntranceCardRepository;
import com.jjld.domain.house.repository.HouseRepository;
import com.jjld.domain.house.specification.HouseSpecification;
import com.jjld.global.exception.house.HouseCardNotFoundException;
import com.jjld.global.exception.house.HouseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public HouseResponse findByIdHouseId(Long houseId) {
        House house = houseDAO.findHouseId(houseId);
        if(houseId == null){
            throw new HouseNotFoundException("없는 세대 번호입니다");
        }

        HouseResponse houseResponse = HouseResponse.builder()
                .houseId(house.getHouseId())
                .houseDong(house.getHouseDong())
                .build();
        return null;
    }

    // 세대 등록
    @Override
    public void houseInsert(Long houseId, HouseManagementResponse houseManagementResponse) {
        House house = houseRepository.findByHouseId(houseId);

        if(house == null){
            throw new HouseNotFoundException("존재하지 않는 세대 입니다.");
        }

        // 등록하는 세대 카드가 없으면 빈 리스트 처리
        List<EntranceCard> houseCardList = Optional.ofNullable(houseManagementResponse.getCardUid())
                .orElse(Collections.emptyList())
                .stream()
                .map(uid -> entranceCardRepository.findByCardUid(uid)
                        .orElseGet(() -> {
                            // DB에 없는 카드면 생성
                            EntranceCard newCard = EntranceCard.builder()
                                    .cardUid(uid)
                                    .status(CardStatus.ACTIVE)
                                    .build();
                            return entranceCardRepository.save(newCard);
                        }))
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

        house.setHouseholderName(houseManagementResponse.getHouseholderName());
        house.setHouseholderPhone(houseManagementResponse.getHouseholderPhone());
        house.setHouseholderEmail(houseManagementResponse.getHouseholderEmail());
        house.setHouseholdSize(houseManagementResponse.getHouseholdSize());
        house.setEntrancePass(houseManagementResponse.getEntrancePass());
        house.setHouseStatus(houseManagementResponse.getHouseholderName() != null
                    && !houseManagementResponse.getHouseholderName().isBlank());

        houseRepository.save(house);

    }


}

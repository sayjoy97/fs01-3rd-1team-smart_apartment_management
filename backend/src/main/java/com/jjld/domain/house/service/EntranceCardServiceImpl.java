package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.EntranceCardResponse;
import com.jjld.domain.house.entity.EntranceCard;
import com.jjld.domain.house.repository.EntranceCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EntranceCardServiceImpl implements EntranceCardService {
    private final EntranceCardRepository entranceCardRepository;

    @Override
    public List<EntranceCardResponse> findAll() {
        List<EntranceCard> cardList = entranceCardRepository.findAll();

        return cardList.stream()
                .map(card -> {
                    Long houseId = null;

                    if (card.getHouse() != null){
                        houseId = card.getHouse().getHouseId();
                    }

                    return new EntranceCardResponse(
                            card.getCardId(),
                            card.getCardUid(),
                            card.getStatus().name(),
                            houseId
                    );
                }).toList();

    }
}

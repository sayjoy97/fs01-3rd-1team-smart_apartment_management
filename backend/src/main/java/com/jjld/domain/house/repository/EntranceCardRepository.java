package com.jjld.domain.house.repository;

import com.jjld.domain.house.entity.EntranceCard;
import com.jjld.domain.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntranceCardRepository extends JpaRepository<EntranceCard, Long> {
    Optional<EntranceCard> findByCardUid(String cardUid);

    EntranceCard findByCardUidAndHouse_HouseDongAndHouse_HouseHo(String cardUid, Integer houseHouseDong, Integer houseHouseHo);

    Integer house(House house);

    EntranceCard findByCardUidAndHouse_HouseDong(String cardUid, Integer houseHouseDong);
}

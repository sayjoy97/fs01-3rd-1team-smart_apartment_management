package com.jjld.domain.house.repository;

import com.jjld.domain.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    House findByHouseId(Long houseId);

}

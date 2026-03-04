package com.jjld.domain.house.repository;

import com.jjld.domain.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<House, Long> {
    Optional<House> findByHouseId(Long houseId);
}

package com.jjld.domain.house.repository;

import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {
    House findByHouseId(Long houseId);

    House findByHouseIdAndHouseholderEmail(Long houseId, String householderEmail);

    @Query("""
        select (h)
        from House h
        where h.houseDong = :houseDong
        and h.houseHo = :houseHo
    """)
    House findByHouseInfo(Integer houseDong, Integer houseHo);

}

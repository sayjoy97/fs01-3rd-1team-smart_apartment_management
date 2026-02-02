package com.jjld.domain.house.dao;

import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HouseDAOImpl implements HouseDAO{
    private final HouseRepository houseRepository;

    @Override
    public House findHouseId(Long houseId) {
        return houseRepository.findByHouseId(houseId);
    }
}

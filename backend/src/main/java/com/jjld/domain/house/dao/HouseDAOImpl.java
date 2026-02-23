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

    // 동호수로 세대아이디 찾아오기
    @Override
    public House findByHouseInfo(Integer houseDong, Integer houseHo) {
        return houseRepository.findByHouseInfo(houseDong, houseHo);
    }
}

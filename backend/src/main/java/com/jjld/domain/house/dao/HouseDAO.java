package com.jjld.domain.house.dao;

import com.jjld.domain.house.entity.House;

public interface HouseDAO {

    // 세대 목록 상세 조회
    House findHouseId(Long houseId);

    // 동호수로 세대아이디 찾아오기
    House findByHouseInfo(Integer houseDong, Integer houseHo);
}

package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.entity.House;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HouseService {
    // 세대 목록조회
    List<HouseResponse> findAll();

    // 세대 목록 상세조회
    HouseResponse findByIdHouseId(Long houseId);

}


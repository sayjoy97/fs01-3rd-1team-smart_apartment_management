package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.HouseManagementResponse;
import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.entity.EntranceCard;

import java.util.List;

public interface HouseService {
    // 세대 목록조회
    List<HouseResponse> findAll();

    // 세대 목록 상세조회
    HouseResponse findByIdHouseId(Long houseId);

    // 세대 등록
    void houseInsert(Long houseId, HouseManagementResponse houseManagementResponse);

}


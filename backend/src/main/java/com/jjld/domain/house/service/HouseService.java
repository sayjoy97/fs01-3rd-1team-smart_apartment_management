package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.HouseDetailResponse;
import com.jjld.domain.house.dto.HouseManagementResponse;
import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.dto.HouseSearchCond;

import java.util.List;

public interface HouseService {
    // 세대 목록조회
    List<HouseResponse> search(HouseSearchCond cond);

    // 세대 목록 상세조회
    HouseDetailResponse getDetail(Long houseId);

    // 세대 등록
    void houseInsert(Long houseId, HouseManagementResponse houseManagementResponse);

}


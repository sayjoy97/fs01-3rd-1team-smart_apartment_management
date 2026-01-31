package com.jjld.domain.parkingfee.service;

import com.jjld.domain.parkingfee.dto.SimpleRateResponse;


public interface VehicleFeeService {
    // 차량 출입관리 페이지 출력용 금일+이번달 요금누적 조회
    SimpleRateResponse getRateByType();
}

package com.jjld.domain.parkingfee.service;

import com.jjld.domain.parkingfee.dto.AllInOneChargeViewResponse;
import com.jjld.domain.parkingfee.dto.Daily30TotalResponse;
import com.jjld.domain.parkingfee.dto.MonthlyTotalResponse;
import com.jjld.domain.parkingfee.dto.SimpleRateResponse;

import java.util.List;


public interface ParkingFeeService {
    // 차량 출입관리 페이지 출력용 금일+이번달 요금누적 조회
    SimpleRateResponse getRateByType();

    // 요금관리 페이지 상단 통합조회
    AllInOneChargeViewResponse getAllInOneChargeView();

    // 최근 30일 일별 누적금액 조회
    List<Daily30TotalResponse> getDaily30Total();

    // 최근 12개월 월별 누적금액 조회
    List<MonthlyTotalResponse> getMonthlyTotal();

    // 최근 3년 연간 누적금액 조회
}

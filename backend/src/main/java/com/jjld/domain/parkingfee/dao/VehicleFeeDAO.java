package com.jjld.domain.parkingfee.dao;

import java.time.LocalDateTime;

public interface VehicleFeeDAO {

    // 조건 날짜별 누적금액 조회
    long getCountByType(LocalDateTime start, LocalDateTime end);
}

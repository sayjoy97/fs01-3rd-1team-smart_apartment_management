package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dto.EntryExitRecordResponse;
import com.jjld.domain.cargate.dto.RecordDetailResponse;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Map;

public interface CargateService {

    // 최근 7일 차량 출입현황 리스트 조회


    // 페이지&개수만큼의 리스트 호출
    Page<EntryExitRecordResponse> getRecordList(int size, int page);

    // 로그아이디 별 상세조회
    RecordDetailResponse getDetailInfo(Long cargate_event_log_id);
}

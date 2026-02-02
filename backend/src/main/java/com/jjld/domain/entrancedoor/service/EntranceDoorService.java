package com.jjld.domain.entrancedoor.service;

import com.jjld.domain.entrancedoor.dto.EntranceGateLogResponse;
import com.jjld.domain.entrancedoor.dto.EntranceGateLogSearchCond;
import com.jjld.domain.entrancedoor.dto.EntranceGateResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EntranceDoorService {
    // 세대 동 조회
    List<EntranceGateResponse> findAll();

    // 공동현관 출입 로그 페이징 조회
    Page<EntranceGateLogResponse> search(EntranceGateLogSearchCond cond, int page, int size);
    
}

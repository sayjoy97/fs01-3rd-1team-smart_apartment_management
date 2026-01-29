package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dao.CargateDAO;
import com.jjld.domain.cargate.dto.DailyVehicleTypeCountResponse;
import com.jjld.domain.cargate.dto.EntryExitRecordResponse;
import com.jjld.domain.cargate.dto.ParkingSessionResponse;
import com.jjld.domain.cargate.dto.RecordDetailResponse;
import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.Vehicle;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jjld.domain.cargate.entity.Enum.GateType.ENTRY;

@Service
@RequiredArgsConstructor
@Builder
public class CargateServiceImpl implements CargateService {
    private final CargateDAO cargateDAO;

    private final ModelMapper modelMapper;

    // 최근 7일 차량 출입현황 리스트 조회
    @Override
    public List<DailyVehicleTypeCountResponse> getDailyVehicleTypeCountList() {
        GateType gateType = ENTRY;
        LocalDate today = LocalDate.now();

        List<DailyVehicleTypeCountResponse> last7DaysCountByTypeList = new ArrayList<>();

        for(int i=0; i<7; i++){
            LocalDate selectedDay = today.minusDays(i);

            Map<VehicleType, Long> countMap = cargateDAO.countByTypeList(gateType, selectedDay);
            for (VehicleType type : VehicleType.values()) {
                countMap.putIfAbsent(type, 0L);
            }
            last7DaysCountByTypeList.add(new DailyVehicleTypeCountResponse(selectedDay, countMap));
        }
        return last7DaysCountByTypeList;
    }

    // 페이지&개수만큼의 리스트 호출
    @Override
    public Page<EntryExitRecordResponse> getRecordList(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventAt").descending());
        return cargateDAO.findAllCargateEventLogs(pageable).map(
                entity -> EntryExitRecordResponse.builder()
                        .cargateEventId(entity.getCargateEventId())
                        .plateNumber(entity.getVehicle().getPlateNumber())
                        .parkingStatus(entity.getGateType().name())
                        .vehicleType(entity.getVehicle().getVehicleType())
                        .eventAt(entity.getEventAt())
                        .build()
        );
    }

    // 로그아이디 별 출입기록 상세조회
    @Override
    public RecordDetailResponse getDetailInfo(Long cargate_event_log_id) {
        CargateEventLog cargateLogById = cargateDAO.findCargateLogById(cargate_event_log_id);

        Vehicle vehicle = cargateLogById.getVehicle();

        List<ParkingSessionResponse> parkingSessionResponses = vehicle.getParkingSessions().stream()
                .map(parkingSession -> new ParkingSessionResponse(
                        parkingSession.getEntryAt(),
                        parkingSession.getExitAt(),
                        parkingSession.getStatus()
                ))
                .toList();

        return RecordDetailResponse.builder()
                .cargateEventId(cargateLogById.getCargateEventId())
                .plateNumber(cargateLogById.getVehicle().getPlateNumber())
                .parkingStatus(cargateLogById.getGateType().name())
                .parkingSessions(parkingSessionResponses)
                .imagePath(cargateLogById.getImagePath())
                .build();
    }
}

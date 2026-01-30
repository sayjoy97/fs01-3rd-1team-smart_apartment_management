package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dao.CargateDAO;
import com.jjld.domain.cargate.dto.*;
import com.jjld.domain.cargate.entity.ApprovedCar;
import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.RegisteredCar;
import com.jjld.domain.cargate.entity.Vehicle;
import com.jjld.domain.cargate.repository.ApprovedCarRepository;
import com.jjld.domain.cargate.repository.RegisteredCarRepository;
import com.jjld.domain.cargate.repository.VehicleRepository;
import com.jjld.domain.house.repository.HouseRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Builder
public class CargateServiceImpl implements CargateService {
    private final CargateDAO cargateDAO;

    private final VehicleRepository vehicleRepository;
    private final ApprovedCarRepository approvedCarRepository;
    private final HouseRepository houseRepository;
    private final RegisteredCarRepository registeredCarRepository;

    private final ModelMapper modelMapper;

    // 최근 7일 차량 출입현황 리스트 조회
    @Override
    public Map<LocalDate, Map<VehicleType, Long>> getLast7DaysEntryStats() {

        LocalDate today = LocalDate.now();

        Map<LocalDate, Map<VehicleType, Long>> result = new LinkedHashMap<>();

        // 최근 7일 (오늘 포함, 오래된 날짜부터)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            Map<VehicleType, Long> raw =
                    cargateDAO.getEntryCountByVehicleType(start, end);

            // 모든 VehicleType 0 보장
            Map<VehicleType, Long> dailyStats = new EnumMap<>(VehicleType.class);
            for (VehicleType type : VehicleType.values()) {
                dailyStats.put(type, raw.getOrDefault(type, 0L));
            }

            result.put(date, dailyStats);
        }

        return result;
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

        return RecordDetailResponse.builder()
                .cargateEventId(cargateLogById.getCargateEventId())
                .plateNumber(cargateLogById.getVehicle().getPlateNumber())
                .parkingStatus(cargateLogById.getGateType().name())
                .entryAt(cargateLogById.getParkingSession().getEntryAt())
                .exitAt(cargateLogById.getParkingSession().getExitAt())
                .status(cargateLogById.getParkingSession().getStatus())
                .imagePath(cargateLogById.getImagePath())
                .build();
    }

    // 컨트롤러에서 직접 요청하는 작업내용
    @Override
    public Long registerVehicle(VehicleRegisterRequest req) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(req.getPlateNumber())
                .orElseGet(() -> vehicleRepository.save(
                        new Vehicle(req.getPlateNumber(), req.getVehicleType())
                ));

        switch (req.getRegisterType()) {
            case 1 -> registerHouseVehicle(vehicle, req);
            case 2 -> registerApprovedVehicle(vehicle, req);
            default -> throw new IllegalArgumentException("잘못된 등록 유형");
        }

        return vehicle.getVehicleId();
    }

    // 세대 등록차량일 때 필요한 작업내용
    @Override
    public void registerHouseVehicle(Vehicle vehicle, VehicleRegisterRequest req) {
        if (req.getHouseId() == null) {
            throw new IllegalArgumentException("세대 정보 필수");
        }

        RegisteredCar registeredCar = RegisteredCar.builder()
                .vehicle(vehicle)
                .house(houseRepository.getReferenceById(req.getHouseId()))
                .build();

        registeredCarRepository.save(registeredCar);
    }

    // 관리자 승인차량 등록일 때 필요한 작업내용
    @Override
    public void registerApprovedVehicle(Vehicle vehicle, VehicleRegisterRequest req) {

        LocalDate startAt = req.getStartAt() != null
                ? req.getStartAt()
                : LocalDate.now();

        ApprovedCar approvedCar = ApprovedCar.builder()
                .vehicle(vehicle)
                .approvalReason(
                        "[" + req.getApprovalType() + "] " + req.getApprovalReason()
                )
                .startAt(startAt)
                .endAt(req.getEndAt())
                .build();

        approvedCarRepository.save(approvedCar);
    }

    // 차량정보 수정
    @Override
    public VehicleRegisterRequest updateCar(Long cargateEventId, VehicleRegisterRequest request) {

        return null;
    }

    // 차량정보 삭제
    @Override
    public boolean deleteCar(Long cargateEventId) {
        return false;
    }
}

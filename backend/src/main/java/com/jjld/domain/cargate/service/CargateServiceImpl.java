package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dao.CargateDAO;
import com.jjld.domain.cargate.dto.*;
import com.jjld.domain.cargate.entity.*;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.repository.ApprovedCarRepository;
import com.jjld.domain.cargate.repository.ParkingSessionRepository;
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

    private final HouseRepository houseRepository;

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
    public Long registerVehicle(VehicleRegisterRequest request) {
        Vehicle vehicle = cargateDAO.findByPlateNumber(request.getPlateNumber())
                .orElseGet(() -> cargateDAO.newVehicle(
                        request.getPlateNumber(),
                        request.getVehicleType()
                        )
                );

        switch (request.getRegisterType()) {
            case 1 -> registerHouseVehicle(vehicle, request);
            case 2 -> registerApprovedVehicle(vehicle, request);
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
                .vehicleOwner(req.getVehicleOwner())
                .build();

        cargateDAO.createRegisteredCar(registeredCar);
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

        cargateDAO.createApprovedCar(approvedCar);
    }

    // 세대 등록차량 조회
    @Override
    public List<RegisteredCarResponse> getRegisteredCars() {
        List<RegisteredCar> registeredList = cargateDAO.findRegisteredList();

        List<RegisteredCarResponse> result = new ArrayList<>();
        for (RegisteredCar car : registeredList) {
            result.add(RegisteredCarResponse.builder()
                    .id(car.getId())
                    .plateNumber(car.getVehicle().getPlateNumber())
                    .vehicleOwner(car.getVehicleOwner())
                    .houseDong(car.getHouse().getHouseDong())
                    .houseHo(car.getHouse().getHouseHo())
                    .vehicleType(car.getVehicle().getVehicleType())
                    .createdAt(car.getCreatedAt())
                    .build()
            );
        }

        // 최신순으로 정렬
        return result.stream()
                .sorted(Comparator.comparing(RegisteredCarResponse::getCreatedAt).reversed())
                .toList();
    }

    // 세대 등록차량 상세정보 조회
    @Override
    public RegisCarDetailResponse getRegisCarDetail(Long vehicle_id) {
        // 아이디로 등록차량 찾기
        RegisteredCar registeredCarEntity = cargateDAO.findRegisteredCarById(vehicle_id);

        // 아이디로 차량 출입기록 리스트 조회
        List<ParkingSession> parkingSessionList = cargateDAO.findByVehicleIdList(vehicle_id);

        // 변환작업
        List<ParkingSessionResponse> sessions = parkingSessionList.stream()
                .map(ps -> ParkingSessionResponse.builder()
                        .parkingSessionId(ps.getParkingSessionId())
                        .entryAt(ps.getEntryAt())
                        .exitAt(ps.getExitAt())
                        .build()
                ).toList();

        return RegisCarDetailResponse.builder()
                .id(registeredCarEntity.getId())
                .plateNumber(registeredCarEntity.getVehicle().getPlateNumber())
                .vehicleType(registeredCarEntity.getVehicle().getVehicleType())
                .parkingSessions(sessions)
                .vehicleOwner(registeredCarEntity.getVehicleOwner())
                .hounsDong(registeredCarEntity.getHouse().getHouseDong())
                .houseHo(registeredCarEntity.getHouse().getHouseHo())
                .createdAt(registeredCarEntity.getCreatedAt())
                .build();
    }

    // 세대 등록차량 삭제
    @Override
    public boolean deleteRegisCar(Long vehicle_id) {
        if(!cargateDAO.deleteByRegisteredCar(vehicle_id)){
            return false;
        }
        cargateDAO.deleteByRegisteredCar(vehicle_id);
        return true;
    }

    // 관리자 승인차량 조회
}

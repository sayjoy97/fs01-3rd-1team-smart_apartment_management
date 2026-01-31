package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dao.*;
import com.jjld.domain.cargate.dto.*;
import com.jjld.domain.cargate.entity.*;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.house.repository.HouseRepository;
import com.jjld.domain.parkingfee.dao.ParkingFeeDAO;
import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Builder
public class CargateServiceImpl implements CargateService {
    private final CargateEventLogDAO cargateEventLogDAO;
    private final VehicleDAO vehicleDAO;
    private final ParkingSessionDAO parkingSessionDAO;
    private final RegisteredDAO registeredDAO;
    private final ApprovedDAO approvedDAO;
    private final ParkingFeeDAO parkingFeeDAO;

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
                    vehicleDAO.getEntryCountByVehicleType(start, end);

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
        return cargateEventLogDAO.findAllCargateEventLogs(pageable).map(
                entity -> EntryExitRecordResponse.builder()
                        .cargateEventId(entity.getCargateEventId())
                        .plateNumber(entity.getVehicle().getPlateNumber())
                        .parkingStatus(entity.getGateType().name())
                        .vehicleType(entity.getVehicle().getVehicleType())
                        .eventAt(entity.getEventAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public LogDetailBaseResponse getLogDetail(Long cargateEventId) {

        CargateEventLog log = cargateEventLogDAO.findCargateEventLogById(cargateEventId);

        ParkingSession session = log.getParkingSession();
        Vehicle vehicle = log.getVehicle();

        LocalDateTime entryAt = session.getEntryAt();
        LocalDateTime exitAt = session.getExitAt();
        LocalDateTime now = LocalDateTime.now();

        long stayMinutes = Duration.between(
                entryAt,
                exitAt != null ? exitAt : now
        ).toMinutes();

        VehicleType type = vehicle.getVehicleType();

        return switch (type) {
            case REGISTERED -> buildRegistered(log, stayMinutes);
            case ADMIN_APPROVED -> buildAdminApproved(log, stayMinutes);
            case UNREGISTERED -> buildUnregistered(log, stayMinutes);
        };
    }

    // REGISTERED 응답 생성
    private LogDetailByRegisResponse buildRegistered(
            CargateEventLog log, long stayMinutes) {

        Vehicle vehicle = log.getVehicle();
        ParkingSession ps = log.getParkingSession();

        RegisteredCar rc = registeredDAO.findByVehicle_VehicleId(vehicle.getVehicleId());

        return LogDetailByRegisResponse.builder()
                .cargateEventId(log.getCargateEventId())
                .plateNumber(vehicle.getPlateNumber())
                .parkingStatus(ps.getStatus())
                .entryAt(ps.getEntryAt())
                .exitAt(ps.getExitAt())
                .stayMinutes(stayMinutes)
                .vehicleType(vehicle.getVehicleType())
                .houseId(rc.getHouse().getHouseId())
                .vehicleOwner(rc.getVehicleOwner())
                .build();
    }

    // ApprovedCar 응답 생성
    private LogDetailByApprovedResponse buildAdminApproved(
            CargateEventLog log, long stayMinutes) {

        Vehicle vehicle = log.getVehicle();
        ParkingSession ps = log.getParkingSession();

        ApprovedCar ac = approvedDAO.findByVehicle_VehicleId(vehicle.getVehicleId());

        return LogDetailByApprovedResponse.builder()
                .cargateEventId(log.getCargateEventId())
                .plateNumber(vehicle.getPlateNumber())
                .parkingStatus(ps.getStatus())
                .entryAt(ps.getEntryAt())
                .exitAt(ps.getExitAt())
                .stayMinutes(stayMinutes)
                .vehicleType(vehicle.getVehicleType())
                .approvalReason(ac.getApprovalReason())
                .createdAt(ac.getCreatedAt())
                .currentStatus(ac.getCurrentStatus())
                .build();
    }

    // UnRegistered 응답생성
    private LogDetailByUnRegisResponse buildUnregistered(
            CargateEventLog log, long stayMinutes) {

        Vehicle vehicle = log.getVehicle();
        ParkingSession ps = log.getParkingSession();

        ParkingFeeSetting setting = parkingFeeDAO.findByFirstActive();

        int fee = calculateFee(stayMinutes, setting, ps.getEntryAt());

        return LogDetailByUnRegisResponse.builder()
                .cargateEventId(log.getCargateEventId())
                .plateNumber(vehicle.getPlateNumber())
                .parkingStatus(ps.getStatus())
                .entryAt(ps.getEntryAt())
                .exitAt(ps.getExitAt())
                .stayMinutes(stayMinutes)
                .vehicleType(vehicle.getVehicleType())
                .calculatedFee(fee)
                .build();
    }

    // 요금계산
    private int calculateFee(long minutes, ParkingFeeSetting s, LocalDateTime entryAt) {

        if (minutes <= s.getBaseTime()) {
            return 0;
        }

        long extra = minutes - s.getBaseTime();
        long unitCount = (long) Math.ceil(
                (double) extra / s.getUnitMinutes()
        );

        int fee = s.getBaseCharge() + (int) unitCount * s.getUnitCharge();

        // 피크 요금 적용
        if (s.getPeakEnabled()) {
            LocalTime entryTime = entryAt.toLocalTime();
            if (!entryTime.isBefore(s.getPeakStartTime())
                    && !entryTime.isAfter(s.getPeakEndTime())) {

                long peakUnits = (long) Math.ceil(
                        (double) extra / s.getPeakUnitMinutes()
                );
                fee = s.getBaseCharge() + (int) peakUnits * s.getPeakUnitCharge();
            }
        }

        return fee;
    }

    // 로그기록 내 정보수정
    @Transactional
    @Override
    public void updateVehicleByLog(
            Long cargateEventId,
            VehicleRelatedRequest request) {

        // 로그 조회
        CargateEventLog log = cargateEventLogDAO.findByLogId(cargateEventId);

        Vehicle vehicle = log.getVehicle();

        if (vehicle == null) {
            throw new IllegalStateException("OCR 실패 로그는 수정 불가");
        }

        // UNREGISTERED 차단
        if (request.getVehicleType() == VehicleType.UNREGISTERED) {
            throw new IllegalArgumentException("미등록 차량은 수정 대상 아님");
        }

        // 차량 번호 수정 (중복 체크 권장)
        vehicleDAO.changePlateNumber(request.getPlateNumber(), vehicle.getVehicleId());

        vehicle.setPlateNumber(request.getPlateNumber());

        VehicleType beforeType = vehicle.getVehicleType();
        VehicleType afterType = request.getVehicleType();

        // 차량 유형 변경 여부 분기
        if (beforeType == afterType) {
            updateSameType(vehicle, afterType, request);
        } else {
            changeVehicleType(vehicle, beforeType, afterType, request);
        }

        // vehicle 타입 최종 반영
        vehicle.setVehicleType(afterType);
    }

    private void updateSameType(Vehicle vehicle, VehicleType type, VehicleRelatedRequest req) {

        if (type == VehicleType.REGISTERED) {
            RegisteredCar rc = registeredDAO.findByVehicle_VehicleId(vehicle.getVehicleId());

            if (req.getHouseId() == null || req.getVehicleOwner() == null) {
                throw new IllegalArgumentException("세대 정보 필수");
            }

            rc.setHouse(houseRepository.getReferenceById(req.getHouseId()));
            rc.setVehicleOwner(req.getVehicleOwner());
        }

        if (type == VehicleType.ADMIN_APPROVED) {
            ApprovedCar ac = approvedDAO.findByVehicle_VehicleId(vehicle.getVehicleId());

            ac.setApprovalReason(formatApprovalReason(req));
            ac.setStartAt(
                    req.getStartAt() != null ? req.getStartAt() : LocalDate.now()
            );
            ac.setEndAt(req.getEndAt());
        }
    }

    private void changeVehicleType(Vehicle vehicle, VehicleType before,
                                   VehicleType after, VehicleRelatedRequest req
    ) {

        // 이전 유형 데이터 제거
        if (before == VehicleType.REGISTERED) {
            registeredDAO.deleteByRegisteredCar(vehicle.getVehicleId());
        }

        if (before == VehicleType.ADMIN_APPROVED) {
            approvedDAO.deleteByApprovedCar(vehicle.getVehicleId());
        }

        // 신규 유형 생성
        if (after == VehicleType.REGISTERED) {

            if (req.getHouseId() == null || req.getVehicleOwner() == null) {
                throw new IllegalArgumentException("세대 정보 필수");
            }

            registeredDAO.createRegisteredCar(
                    RegisteredCar.builder()
                            .vehicle(vehicle)
                            .house(houseRepository.getReferenceById(req.getHouseId()))
                            .vehicleOwner(req.getVehicleOwner())
                            .build()
            );
        }

        if (after == VehicleType.ADMIN_APPROVED) {

            ApprovedCar approvedCar = ApprovedCar.builder()
                    .vehicle(vehicle)
                    .approvalReason(formatApprovalReason(req))
                    .startAt(
                            req.getStartAt() != null
                                    ? req.getStartAt()
                                    : LocalDate.now()
                    )
                    .endAt(req.getEndAt())
                    .build();

            approvedDAO.createApprovedCar(approvedCar);
        }
    }

    private String formatApprovalReason(VehicleRelatedRequest req) {

        if (req.getApprovalType() == null || req.getApprovalReason() == null) {
            throw new IllegalArgumentException("승인 사유 정보 부족");
        }

        return "[" + req.getApprovalType() + "] " + req.getApprovalReason();
    }


    // 컨트롤러에서 직접 요청하는 작업내용
    @Override
    public Long registerVehicle(VehicleRelatedRequest request) {
        Vehicle vehicle = vehicleDAO.findByPlateNumber(request.getPlateNumber())
                .orElseGet(() -> vehicleDAO.newVehicle(
                        request.getPlateNumber(),
                        request.getVehicleType()
                        )
                );

        switch (request.getVehicleType()) {
            case REGISTERED -> registerHouseVehicle(vehicle, request);
            case ADMIN_APPROVED -> registerApprovedVehicle(vehicle, request);
            default -> throw new IllegalArgumentException("잘못된 등록 유형");
        }

        return vehicle.getVehicleId();
    }

    // 세대 등록차량일 때 필요한 작업내용
    @Override
    public void registerHouseVehicle(Vehicle vehicle, VehicleRelatedRequest req) {
        if (req.getHouseId() == null) {
            throw new IllegalArgumentException("세대 정보 필수");
        }

        RegisteredCar registeredCar = RegisteredCar.builder()
                .vehicle(vehicle)
                .house(houseRepository.getReferenceById(req.getHouseId()))
                .vehicleOwner(req.getVehicleOwner())
                .build();

        registeredDAO.createRegisteredCar(registeredCar);
    }

    // 관리자 승인차량 등록일 때 필요한 작업내용
    @Override
    public void registerApprovedVehicle(Vehicle vehicle, VehicleRelatedRequest req) {

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

        approvedDAO.createApprovedCar(approvedCar);
    }

    // 세대 등록차량 조회
    @Override
    public List<RegisCarResponse> getRegisteredCars() {
        List<RegisteredCar> registeredList = registeredDAO.findRegisteredList();

        List<RegisCarResponse> result = new ArrayList<>();
        for (RegisteredCar car : registeredList) {
            result.add(RegisCarResponse.builder()
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
                .sorted(Comparator.comparing(RegisCarResponse::getCreatedAt).reversed())
                .toList();
    }

    // 세대 등록차량 상세정보 조회
    @Override
    public RegisCarDetailResponse getRegisCarDetail(Long vehicle_id) {
        // 아이디로 등록차량 찾기
        RegisteredCar registeredCarEntity = registeredDAO.findByVehicle_VehicleId(vehicle_id);

        // 아이디로 차량 출입기록 리스트 조회
        List<ParkingSession> parkingSessionList = parkingSessionDAO.findByVehicleIdList(vehicle_id);

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
        if(!registeredDAO.deleteByRegisteredCar(vehicle_id)){
            return false;
        }

        registeredDAO.deleteByRegisteredCar(vehicle_id);
        return true;
    }

    // 관리자 승인차량 조회 리스트
    @Override
    public List<ApprovedCarResponse> ApprovedCarList() {

        return approvedDAO.ApprovedCarList().stream()
                .map(car -> ApprovedCarResponse.builder()
                        .id(car.getId())
                        .plateNumber(car.getVehicle().getPlateNumber())
                        .currentStatus(car.getCurrentStatus())
                        .craetedAt(car.getCreatedAt())
                        .build())
                .toList();
    }

    // 관리자 승인차량 상세정보 조회
    @Override
    public ApprovedCarDetailResponse getApprovedCarDetail(Long vehicle_id) {
        ApprovedCar approvedCarById = approvedDAO.findByVehicle_VehicleId(vehicle_id);

        // 아이디로 차량 출입기록 리스트 조회
        List<ParkingSession> parkingSessionList = parkingSessionDAO.findByVehicleIdList(vehicle_id);

        // 변환작업
        List<ParkingSessionResponse> sessions = parkingSessionList.stream()
                .map(ps -> ParkingSessionResponse.builder()
                        .parkingSessionId(ps.getParkingSessionId())
                        .entryAt(ps.getEntryAt())
                        .exitAt(ps.getExitAt())
                        .build()
                ).toList();

        return ApprovedCarDetailResponse.builder()
                .id(approvedCarById.getId())
                .plateNumber(approvedCarById.getVehicle().getPlateNumber())
                .vehicleType(approvedCarById.getVehicle().getVehicleType())
                .currentStatus(approvedCarById.getCurrentStatus())
                .parkingSessions(sessions)
                .approvalReason(approvedCarById.getApprovalReason())
                .createdAt(approvedCarById.getCreatedAt())
                .startAt(approvedCarById.getStartAt())
                .endAt(approvedCarById.getEndAt())
                .build();
    }

    // 관리자 승인차량 수정
    @Override
    public void updateApprovedCar(Long vehicle_id, ApprovedCarRequest request) {
        ApprovedCar approvedCarById = approvedDAO.findByVehicle_VehicleId(vehicle_id);

        approvedCarById.setApprovalReason(request.getApprovalReason());
        approvedCarById.setStartAt(request.getStartAt());
        approvedCarById.setEndAt(request.getEndAt());

        approvedDAO.updateApprovedCar(approvedCarById);
    }

    // 관리자 승인차량 삭제
    @Override
    public Boolean deleteApprovedCar(Long vehicle_id) {
        if(!approvedDAO.deleteByApprovedCar(vehicle_id)){
            return false;
        }
        approvedDAO.deleteByApprovedCar(vehicle_id);
        return true;
    }
}

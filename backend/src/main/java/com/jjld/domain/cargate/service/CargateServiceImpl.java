package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dao.*;
import com.jjld.domain.cargate.dto.*;
import com.jjld.domain.cargate.entity.*;
import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.house.dao.HouseDAO;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.parkingfee.dao.ParkingFeeDAO;
import com.jjld.domain.parkingfee.dao.ParkingFeeSettingDAO;
import com.jjld.domain.parkingfee.entity.ParkingFeeHistory;
import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import com.jjld.global.mqtt.MqttPublish;
import com.jjld.global.mqtt.handler.cargate.CargateServiceType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CargateServiceImpl implements CargateService {
    private static final Logger log = LoggerFactory.getLogger(CargateServiceImpl.class);
    private final CargateEventLogDAO cargateEventLogDAO;
    private final VehicleDAO vehicleDAO;
    private final ParkingSessionDAO parkingSessionDAO;
    private final RegisteredDAO registeredDAO;
    private final ApprovedDAO approvedDAO;
    private final ParkingFeeDAO parkingFeeDAO;
    private final CargateDAO cargateDAO;
    private final ParkingFeeSettingDAO parkingFeeSettingDAO;
    private final HouseDAO houseDAO;

    private final MqttPublish mqttPublish;

    // 최근 7일 차량 출입현황 리스트 조회
    @Override
    public Map<LocalDate, Map<VehicleType, Long>> getLast7DaysEntryStats() {

        // 오늘날짜
        LocalDate today = LocalDate.now();

        // 유형별 카운트를 최근 7일동안의 데이터를 가져오기 위한 Map 선언
        Map<LocalDate, Map<VehicleType, Long>> result = new LinkedHashMap<>();

        // 최근 7일 (오늘 포함, 오래된 날짜부터)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            // dao에서 설정날짜 내 카운트 추출
            Map<VehicleType, Long> raw = vehicleDAO.getEntryCountByVehicleType(start, end);

            // VehicleType으로 map세팅
            Map<VehicleType, Long> dailyStats = new EnumMap<>(VehicleType.class);

            for (VehicleType type : VehicleType.values()) {
                dailyStats.put(type, raw.getOrDefault(type, 0L)); // 모든 VehicleType 0 보장
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

    // 로그기록별 상세조회
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
    private LogDetailByRegisResponse buildRegistered(CargateEventLog log, long stayMinutes) {

        Vehicle vehicle = log.getVehicle(); // cargateEventLog테이블에서 vehicle정보 가져오기
        ParkingSession ps = log.getParkingSession(); // cargateEventLog테이블에서 parkingSession정보 가져오기

        RegisteredCar rc = registeredDAO.findByVehicle_VehicleId(vehicle.getVehicleId());

        String houseInfo = rc.getHouse().getHouseDong() + "동 " + rc.getHouse().getHouseHo() + "호";

        return LogDetailByRegisResponse.builder()
                .cargateEventId(log.getCargateEventId())
                .plateNumber(vehicle.getPlateNumber())
                .vehicleId(vehicle.getVehicleId())
                .parkingStatus(ps.getStatus())
                .image_path(log.getImagePath())
                .entryAt(ps.getEntryAt())
                .exitAt(ps.getExitAt())
                .stayMinutes(stayMinutes)
                .vehicleType(vehicle.getVehicleType())
                .houseInfo(houseInfo)
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
                .vehicleId(vehicle.getVehicleId())
                .image_path(log.getImagePath())
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


        ParkingFeeSetting setting = parkingFeeSettingDAO.findAppliedSetting(ps.getEntryAt());

        int fee = calculateFee(stayMinutes, setting, ps.getEntryAt());

        return LogDetailByUnRegisResponse.builder()
                .cargateEventId(log.getCargateEventId())
                .plateNumber(vehicle.getPlateNumber())
                .vehicleId(vehicle.getVehicleId())
                .image_path(log.getImagePath())
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
            if (!entryTime.isBefore(s.getPeakStartTime()) && !entryTime.isAfter(s.getPeakEndTime())) {

                long peakUnits = (long) Math.ceil((double) extra / s.getPeakUnitMinutes());

                fee = s.getBaseCharge() + (int) peakUnits * s.getPeakUnitCharge();
            }
        }

        return fee;
    }

    // 로그기록 내 정보수정
    @Transactional
    @Override
    public void updateVehicleByLog(Long cargateEventId, VehicleRelatedRequest request) {

        // 로그 조회
        CargateEventLog log = cargateEventLogDAO.findByLogId(cargateEventId);

        Vehicle vehicle = log.getVehicle();

        if (vehicle == null) {
            throw new IllegalStateException("OCR 실패 로그는 수정 불가");
        }

        if (request.getVehicleType() == VehicleType.UNREGISTERED) {
            updateToUnregistered(vehicle, request);

        } else if (request.getVehicleType() == VehicleType.REGISTERED) {
            updateToRegistered(vehicle, request);

        } else if (request.getVehicleType() == VehicleType.ADMIN_APPROVED) {

            updateToApproved(vehicle, request);
        }
    }

    // -> 미등록차량으로 변경
    private void updateToUnregistered(Vehicle vehicle, VehicleRelatedRequest request) {
        Vehicle byVehicleId = vehicleDAO.findByVehicleId(vehicle.getVehicleId());

        switch (vehicle.getVehicleType()) {
            case UNREGISTERED:
                new IllegalArgumentException("이미 미등록 차량입니다.");
                break;
            case REGISTERED:
                registeredDAO.deleteByRegisteredCar(vehicle.getVehicleId());

                byVehicleId.setVehicleType(VehicleType.UNREGISTERED);
                break;
            case ADMIN_APPROVED:
                approvedDAO.deleteByApprovedCar(vehicle.getVehicleId());

                byVehicleId.setVehicleType(VehicleType.UNREGISTERED);
                break;
        }
    }

    // -> 세대등록 차량으로 변경
    private void updateToRegistered(Vehicle vehicleEntity, VehicleRelatedRequest request) {
        Vehicle vehicle = vehicleDAO.findByPlateNumber(request.getPlateNumber())
                .orElseGet(() -> vehicleDAO.newVehicle(
                                request.getPlateNumber(),
                                request.getVehicleType()
                        )
                );

        switch (vehicle.getVehicleType()) {
            case UNREGISTERED:
                vehicle.setVehicleType(VehicleType.REGISTERED);
                registerHouseVehicle(vehicle, request);

                break;
            case REGISTERED:
                String houseInfo = request.getHouseInfo().replace("동", "").replace("호", "");
                String[] info = houseInfo.split(" ");
                Integer Dong = Integer.valueOf(info[0]);
                Integer Ho = Integer.valueOf(info[1]);


                RegisteredCar updateEntity = registeredDAO.findByVehicle_VehicleId(vehicle.getVehicleId());
                House findHouse = houseDAO.findByHouseInfo(Dong, Ho);

                updateEntity.setVehicleOwner(request.getVehicleOwner());
                updateEntity.setHouse(findHouse);

                registeredDAO.updateRegisteredCar(updateEntity);
                break;
            case ADMIN_APPROVED:
                // 승인차량 내용 지우고
                approvedDAO.deleteByApprovedCar(vehicle.getVehicleId());

                Vehicle byPlateNumber = vehicleDAO.findByPlateNumber(vehicleEntity.getPlateNumber())
                        .orElseThrow(() -> new IllegalStateException("차량이 존재하지 않습니다."));

                byPlateNumber.setVehicleType(VehicleType.REGISTERED);

                registerHouseVehicle(byPlateNumber, request);

                break;
        }
    }

    // -> 관리자 승인차량으로 변경
    private void updateToApproved(Vehicle vehicleEntity, VehicleRelatedRequest request) {
        Vehicle vehicle = vehicleDAO.findByPlateNumber(request.getPlateNumber())
                .orElseGet(() -> vehicleDAO.newVehicle(
                                request.getPlateNumber(),
                                request.getVehicleType()
                        )
                );

        switch (vehicleEntity.getVehicleType()) {
            case UNREGISTERED:
                vehicle.setVehicleType(VehicleType.ADMIN_APPROVED);

                registerApprovedVehicle(vehicle, request);

                break;
            case REGISTERED:
                registeredDAO.deleteByRegisteredCar(vehicle.getVehicleId());

                vehicle.setVehicleType(VehicleType.ADMIN_APPROVED);

                registerApprovedVehicle(vehicle, request);
                break;
            case ADMIN_APPROVED:
                ApprovedCar updateEntity = approvedDAO.findByVehicle_VehicleId(vehicle.getVehicleId());

                updateEntity.setApprovalReason(request.getApprovalReason());
                if (request.getStartAt() == null){
                    request.setStartAt(LocalDate.now());
                }
                if(request.getEndAt() == null){
                    LocalDate lastDayOfYear = LocalDate.of(LocalDate.now().getYear(), 12, 31);
                    request.setEndAt(lastDayOfYear);
                }
                updateEntity.setStartAt(request.getStartAt());
                updateEntity.setEndAt(request.getEndAt());

                approvedDAO.updateApprovedCar(updateEntity);
                break;
        }
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
    public void registerHouseVehicle(Vehicle vehicle, VehicleRelatedRequest request) {
        Vehicle managedVehicle = vehicleDAO.findByVehicleId(vehicle.getVehicleId());
        if (request.getHouseInfo() == null) {
            throw new IllegalArgumentException("세대 정보 필수");
        }

        String houseInfo = request.getHouseInfo().replace("동", "").replace("호", "");
        String[] info = houseInfo.split(" ");
        Integer Dong = Integer.valueOf(info[0]);
        Integer Ho = Integer.valueOf(info[1]);
        House findHouse = houseDAO.findByHouseInfo(Dong, Ho);

        RegisteredCar registeredCar = RegisteredCar.builder()
                .vehicle(managedVehicle)
                .house(findHouse)
                .vehicleOwner(request.getVehicleOwner())
                .build();

        registeredDAO.createRegisteredCar(registeredCar);
    }

    // 관리자 승인차량 등록일 때 필요한 작업내용
    @Override
    public void registerApprovedVehicle(Vehicle vehicle, VehicleRelatedRequest req) {
        Vehicle managedVehicle = vehicleDAO.findByVehicleId(vehicle.getVehicleId());

        LocalDate startAt = req.getStartAt() != null
                ? req.getStartAt()
                : LocalDate.now();

        ApprovedCar approvedCar = ApprovedCar.builder()
                .vehicle(managedVehicle)
                .approvalReason(req.getApprovalReason())
                .startAt(startAt)
                .endAt(req.getEndAt())
                .build();

        approvedDAO.createApprovedCar(approvedCar);
    }

    // 세대 등록차량 조회
    @Override
    public Page<RegisCarListResponse> getRegisteredCars(int size, int page) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return registeredDAO.findRegisteredList(pageable).map(
                entity -> RegisCarListResponse.builder()
                        .id(entity.getId())
                        .plateNumber(entity.getVehicle().getPlateNumber())
                        .vehicleOwner(entity.getVehicleOwner())
                        .houseDong(entity.getHouse().getHouseDong())
                        .houseHo(entity.getHouse().getHouseHo())
                        .vehicleType(entity.getVehicle().getVehicleType())
                        .createdAt(entity.getCreatedAt())
                        .build()
        );
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

        // houseInfo정보 넘기기
        String houseInfo = registeredCarEntity.getHouse().getHouseDong() + "동 " + registeredCarEntity.getHouse().getHouseHo() + "호";

        return RegisCarDetailResponse.builder()
                .id(registeredCarEntity.getId())
                .plateNumber(registeredCarEntity.getVehicle().getPlateNumber())
                .vehicleType(registeredCarEntity.getVehicle().getVehicleType())
                .parkingSessions(sessions)
                .vehicleOwner(registeredCarEntity.getVehicleOwner())
                .houseInfo(houseInfo)
                .createdAt(registeredCarEntity.getCreatedAt())
                .build();
    }

    // 세대 등록차량 삭제
    @Override
    public boolean deleteRegisCar(Long vehicle_id) {
        Vehicle vehicleEntity = vehicleDAO.findByVehicleId(vehicle_id);
        vehicleEntity.setVehicleType(VehicleType.UNREGISTERED);

        return registeredDAO.deleteByRegisteredCar(vehicleEntity.getVehicleId());
    }

    // 관리자 승인차량 조회 리스트
    @Override
    public Page<ApprovedCarListResponse> ApprovedCarList(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return approvedDAO.ApprovedCarList(pageable).map(
                entity -> ApprovedCarListResponse.builder()
                .id(entity.getId())
                .plateNumber(entity.getVehicle().getPlateNumber())
                .currentStatus(entity.getCurrentStatus())
                .craetedAt(entity.getCreatedAt())
                .build()
        );
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
        Vehicle byVehicleId = vehicleDAO.findByVehicleId(vehicle_id);
        byVehicleId.setVehicleType(VehicleType.UNREGISTERED);

        return approvedDAO.deleteByApprovedCar(vehicle_id);
    }

    // 입출차 처리
    @Override
    public void AddToTheAccessLog(String payload, CargateServiceType serviceType) {
        // AI 서버 파일명 규격: 20260226_173010_entry_150_78더3456_78더3456.jpg
        String[] parts = payload.split("_");

        // 1. 시간 파싱 (index 0: 날짜, index 1: 시간)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        String dateTimeStr = parts[0] + " " + parts[1]; // "20260226 173010"
        LocalDateTime resultTime = LocalDateTime.parse(dateTimeStr, formatter);

        // 2. 번호판 추출 (index 4: 차번호)
        String rawPlate = parts[4].split("\\.")[0];

        String plateNumber = rawPlate.replaceFirst("[가-힣]", "$0 ");

        // 3. 차량 및 게이트 정보 조회
        Vehicle findVehicle = vehicleDAO.findByPlateNumber(plateNumber)
                .orElseGet(() -> vehicleDAO.newVehicle(plateNumber, VehicleType.UNREGISTERED));

        Cargate cg = cargateDAO.findByCargateType(GateType.valueOf(serviceType.toString()));

        // 이미지 파일명 및 경로 설정 (AI 서버 파일명 그대로 사용)
        String imgFile = payload;
        String imgPath = "cargate_image/" + imgFile;

        String message = "";
        String topic = "";

        // --- [입차 로직: 기존과 동일] ---
        if (cg.getCargateId() == 1) {
            ParkingSession existingSession = parkingSessionDAO.findByVehicleIdEntryStatus(findVehicle.getVehicleId());
            if (existingSession != null) {
                mqttPublish.sendToMqtt("duplicate", "jjld/cargate/entry/gate_command");
                return;
            }
            message = "open_" + findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType().toString();
            topic = "jjld/cargate/entry/gate_command";
            mqttPublish.sendToMqtt(message, topic);

            ParkingSession sessionInfo = parkingSessionDAO.createSessionInfo(ParkingSession.builder()
                    .vehicle(findVehicle).entryCargate(cg).entryAt(resultTime).status(ParkingStatus.IN).build());

            cargateEventLogDAO.createCargateLog(CargateEventLog.builder()
                    .carGate(cg).vehicle(findVehicle).parkingSession(sessionInfo)
                    .gateType(GateType.ENTRY).eventAt(resultTime).imagePath(imgPath).build());
        }

        // --- [출차 로직: 기존과 동일] ---
        else if (cg.getCargateId() == 2) {
            topic = "jjld/cargate/exit/gate_command";
            ParkingSession psEntity = parkingSessionDAO.findByVehicleIdEntryStatus(findVehicle.getVehicleId());
            if (psEntity == null) return;

            switch (findVehicle.getVehicleType()) {
                case REGISTERED, ADMIN_APPROVED:
                    message = "open_" + findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType();
                    psEntity.exit(cg, resultTime);
                    parkingSessionDAO.exitVehicleStatus(psEntity);
                    cargateEventLogDAO.createCargateLog(CargateEventLog.builder()
                            .carGate(cg).vehicle(findVehicle).parkingSession(psEntity)
                            .gateType(GateType.EXIT).eventAt(resultTime).imagePath(imgPath).build());
                    break;

                case UNREGISTERED:
                    long stayMin = java.time.Duration.between(psEntity.getEntryAt(), resultTime).toMinutes();
                    int fee = FeeCount(psEntity.getEntryAt(), resultTime);
                    String currentTime = resultTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                    if (stayMin <= 30) {
                        message = "open_" + findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType() + "_" + stayMin;
                    } else {
                        // 마지막에 imgFile(원본 파일명)을 추가하여 전송
                        message = "request_payment_" + findVehicle.getPlateNumber() + "_" +
                                findVehicle.getVehicleType() + "_" + stayMin + "_" + fee + "_" + currentTime + "_" + imgFile;
                    }
                    break;
            }
            mqttPublish.sendToMqtt(message, topic);
        }
    }

    private int FeeCount(LocalDateTime entryAt, LocalDateTime exitAt) {
        // 1. 현재 활성화된 요금 설정 가져오기
        ParkingFeeSetting setting = parkingFeeSettingDAO.findAppliedSetting(entryAt);
        if (setting == null) return 0;

        // 2. 총 주차 시간(분) 계산
        long totalMinutes = Duration.between(entryAt, exitAt).toMinutes();

        // 3. 무료 주차 시간 확인
        if (totalMinutes <= setting.getBaseTime()) {
            return 0;
        }

        // 4. 요금 계산 시작
        int finalFee = 0;
        long billableMinutes = totalMinutes - setting.getBaseTime(); // 무료 시간 제외

        // 피크 시간 적용 여부 판단
        boolean isPeakApplied = false;
        if (setting.getPeakEnabled()) {
            LocalTime entryTime = entryAt.toLocalTime();
            // 입차 시간이 피크 시간대(Start ~ End) 사이인 경우
            if (!entryTime.isBefore(setting.getPeakStartTime()) && !entryTime.isAfter(setting.getPeakEndTime())) {
                isPeakApplied = true;
            }
        }

        if (isPeakApplied) {
            // 피크 요금 적용: 기본요금 + (추가시간 / 피크단위시간 * 피크단위요금)
            long peakUnits = (long) Math.ceil((double) billableMinutes / setting.getPeakUnitMinutes());
            finalFee = setting.getBaseCharge() + ((int) peakUnits * setting.getPeakUnitCharge());
            log.info("[Peak Fee] Applied. Total: {}원", finalFee);
        } else {
            // 일반 요금 적용: 기본요금 + (추가시간 / 일반단위시간 * 일반단위요금)
            long normalUnits = (long) Math.ceil((double) billableMinutes / setting.getUnitMinutes());
            finalFee = setting.getBaseCharge() + ((int) normalUnits * setting.getUnitCharge());
            log.info("[Normal Fee] Applied. Total: {}원", finalFee);
        }

        return finalFee;
    }

    // 요금 정산완료시 처리
    @Override
    @Transactional
    public void FeeSettlement(String payload, CargateServiceType serviceType) {
        String[] data = payload.split("_");
        String plateNumber = data[0];
        String exitTimeStr = data[1];
        String feeTotal = data[2];
        String originalFileName = data[3];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime exitAt = LocalDateTime.parse(exitTimeStr, formatter);

        // 1. 차량 조회
        Vehicle vehicle = vehicleDAO.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new NoSuchElementException("차량을 찾을 수 없습니다: " + plateNumber));

        // 2. 현재 입차 중인 주차 세션 조회
        ParkingSession ps = parkingSessionDAO.findByVehicleIdEntryStatus(vehicle.getVehicleId());
        if (ps == null) {
            log.error("정산 대상 세션이 없습니다: {}", plateNumber);
            return;
        }

        ParkingFeeSetting feeSetting = parkingFeeSettingDAO.getFeeSetting();

        Duration duration = Duration.between(ps.getEntryAt(), exitAt);

        Integer stayMinutes = (int) duration.toMinutes();

        // 3. 출차 처리 (세션 종료 및 상태 변경)
        Cargate exitGate = cargateDAO.findByCargateType(GateType.EXIT);
        ps.exit(exitGate, exitAt);
        parkingSessionDAO.exitVehicleStatus(ps);

        // 4. 출차 이벤트 로그 기록
        cargateEventLogDAO.createCargateLog(CargateEventLog.builder()
                .carGate(exitGate)
                .vehicle(vehicle)
                .parkingSession(ps)
                .gateType(GateType.EXIT)
                .eventAt(exitAt)
                // 하드코딩된 문자열 대신 원본 파일명을 사용하여 경로 생성
                .imagePath("cargate_image/" + originalFileName)
                .build());

        parkingFeeDAO.createFeeHistory(ParkingFeeHistory.builder()
                .parkingSession(ps)
                .feeSetting(feeSetting)
                .totalMinutes(stayMinutes)
                .totalCharge(Integer.parseInt(feeTotal))
                .paid(true)
                .chargedAt(exitAt)
                .build());

        log.info("결제 완료 및 출차 처리 성공: {}", plateNumber);
    }
}

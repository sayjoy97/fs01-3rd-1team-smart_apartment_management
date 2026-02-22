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

        return LogDetailByRegisResponse.builder()
                .cargateEventId(log.getCargateEventId())
                .plateNumber(vehicle.getPlateNumber())
                .vehicleId(vehicle.getVehicleId())
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
                .vehicleId(vehicle.getVehicleId())
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
            updateToUnregistered(vehicle);

        } else if (request.getVehicleType() == VehicleType.REGISTERED) {
            updateToRegistered(vehicle, request);

        } else if (request.getVehicleType() == VehicleType.ADMIN_APPROVED) {

            updateToApproved(vehicle, request);
        }
    }

    // -> 미등록차량으로 변경
    private void updateToUnregistered(Vehicle vehicle) {
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
        Vehicle vehicle = vehicleDAO.findByPlateNumber(vehicleEntity.getPlateNumber())
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
        Vehicle vehicle = vehicleDAO.findByPlateNumber(vehicleEntity.getPlateNumber())
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");

        String timeStr = payload.split("_")[0];

        // 처리결과 : 시간
        LocalDateTime resultTime = LocalDateTime.parse(timeStr, formatter);

        // 처리결과 : 차량번호.jpg에서 차량번호만 추출
        String plateNumber = payload.split("_")[3].split("\\.")[0];

        // 차량 유형별로 찾기
        Vehicle findVehicle = vehicleDAO.findByPlateNumber(plateNumber)
                .orElse(vehicleDAO.newVehicle(plateNumber, VehicleType.UNREGISTERED)); // 없으면 새로 추가(미등록)

        // 엔티티가 있다면 로그 출력
        if(findVehicle != null){
            log.info("vehicle Type : {}", findVehicle.getVehicleType());
            log.info("plate number : {}", findVehicle.getPlateNumber());
        }

        // 게이트 타입 찾기
        Cargate cg = cargateDAO.findByCargateType(GateType.valueOf(serviceType.toString()));

        // 저장 이미지 경로
        String imgPath = "/cargate_image/entry" + payload;

        String message = "";
        String topic = "";

        // 입차시
        if(cg.getCargateId() == 1){

            message = "open_"+ findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType().toString();

            topic = "jjld/cargate/entry/gate_command";

            // 게이트 오픈 메세지 브로커로 pub
            mqttPublish.sandToMqtt(message, topic);

            // parking_session 엔티티에 입차내용 등록
            ParkingSession sessionInfo = parkingSessionDAO.createSessionInfo(ParkingSession.builder()
                    .vehicle(findVehicle)
                    .entryCargate(cg)
                    .entryAt(resultTime)
                    .status(ParkingStatus.IN)
                    .build());

            // 로그 엔티티에 데이터 추가
            cargateEventLogDAO.createCargateLog(
                    CargateEventLog.builder()
                            .carGate(cg)
                            .vehicle(findVehicle)
                            .parkingSession(sessionInfo)
                            .gateType(GateType.ENTRY)
                            .eventAt(resultTime)
                            .imagePath(imgPath)
                            .build()
            );

        }

        // 출차시
        else if (cg.getCargateId() == 2){
            topic = "jjld/cargate/exit/gate_command";

            // 유형에 따라 요금부여를 위해
            switch (findVehicle.getVehicleType()){
                // 세대등록 및 관리자 승인차량은 게이트 오픈명령 보내고 출차기록 데이터 추가
                case REGISTERED, ADMIN_APPROVED:
                    message = "open_" + findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType();

                    // prarknig_session 엔티티에서 해당차량 입차했던 내용 조회
                    ParkingSession psEntity = parkingSessionDAO.findByVehicleIdEntryStatus(findVehicle.getVehicleId());

                    psEntity.exit(cg, resultTime);

                    // 출차기록 추가
                    parkingSessionDAO.exitVehicleStatus(psEntity);

                    // cargate_event_log 엔티티에 출차기록 추가
                    CargateEventLog logEntity = CargateEventLog.builder()
                            .carGate(cg)
                            .vehicle(findVehicle)
                            .parkingSession(psEntity)
                            .gateType(GateType.EXIT)
                            .eventAt(resultTime)
                            .imagePath(imgPath)
                            .build();

                    cargateEventLogDAO.createCargateLog(logEntity);

                    break;

                // 미등록 차량은 요금정산 요청 메세지 보내기
                case UNREGISTERED:
                    // prarknig_session 엔티티에서 해당차량 입차했던 내용 조회
                    ParkingSession psEntity1 = parkingSessionDAO.findByVehicleIdEntryStatus(findVehicle.getVehicleId());

                    // 출차요청 시간과 입차시간 비교
                    LocalTime entry = psEntity1.getEntryAt().toLocalTime();
                    int entryMinuite = entry.getHour()*60 + entry.getMinute();
                    int resultMinuite = resultTime.toLocalTime().getHour()*60 + resultTime.toLocalTime().getMinute();
                    int result = resultMinuite-entryMinuite;
                    if (result <= 30){
                        message = "open_" + findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType()+ "_" + result;
                    }
                    else{
                        message = "request_payment_" + findVehicle.getPlateNumber() + "_" + findVehicle.getVehicleType();
                    }

            }

            // 각 유형별 다른 메세지를 담은 토픽 브로커로 pub
            mqttPublish.sandToMqtt(message, topic);
        }

    }

    private long FeeCount(LocalTime entryAt, LocalTime endAt){

        // 현재 활성화상태인 요금정산 찾기
        ParkingFeeSetting byFirstActive = parkingFeeDAO.findByFirstActive();

        // 입차시간 출차시간 "분"으로 바꾸기
        int entryM = entryAt.getHour()*60 + entryAt.getMinute();
        int endM = endAt.getHour()*60 + endAt.getMinute();

        if (byFirstActive.getPeakEnabled()){
            return 0;
        } else{
            return 0;
        }
    }

    // 요금 정산완료시 처리
    @Override
    public void FeeSettlement(String payload, CargateServiceType serviceType) {
        String plateNumber = payload.split("_")[1];

        // 차량번호로 차량 찾기
        Vehicle findVehicle = vehicleDAO.findByPlateNumber(plateNumber  )
                .orElseThrow(() -> new IllegalStateException("Not Found"));

        // log엔티티에서 해당정보 찾기
        cargateEventLogDAO.findVehicleByType(findVehicle.getVehicleId(), GateType.EXIT);



    }
}

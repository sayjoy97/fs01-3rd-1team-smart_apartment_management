package com.jjld.domain.cargate.controller;

import com.jjld.domain.cargate.dto.*;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.service.CargateService;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cargate/api")
@RequiredArgsConstructor
public class CargateController {
    private final CargateService cargateService;

    // 최근 7일 유형별 카운트 조회
    @GetMapping("/lastweek")
    @Operation(summary = "최근 7일 유형별 카운트 조회")
    ResponseEntity<?> getLastWeekList() {
        Map<LocalDate, Map<VehicleType, Long>> last7DaysEntryStats = cargateService.getLast7DaysEntryStats();
        return ResponseEntity.ok(ApiResponse.success(last7DaysEntryStats));
    }

    // 백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회
    @GetMapping("/gateRecord/list")
    @Operation(summary = "백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회")
    ResponseEntity<?> getGateRecordlist(
            @RequestParam(name = "size", defaultValue = "10")int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        Page<EntryExitRecordResponse> recordList = cargateService.getRecordList(size, page-1);
        return ResponseEntity.ok(ApiResponse.success(recordList));
    }

    // 차량 출입기록 상세정보 조회
    @GetMapping("/{cargate_event_log_id}/detail")
    @Operation(summary = "출입기록 로그별 상세조회")
    ResponseEntity<?> getDetail(@PathVariable("cargate_event_log_id") Long cargate_event_log_id) {
        LogDetailBaseResponse logDetail = cargateService.getLogDetail(cargate_event_log_id);
        return ResponseEntity.ok(ApiResponse.success(logDetail));
    }

    // 출입기록 로그별 정보수정
    @PutMapping("/{cargate_event_log_id}/update")
    @Operation(summary = "출입기록 로그별 정보수정")
    ResponseEntity<?> updateDetailByLogId(
            @PathVariable("cargate_event_log_id") Long cargateEventLogId,
            @RequestBody VehicleRelatedRequest request) {
        cargateService.updateVehicleByLog(cargateEventLogId, request);
        return ResponseEntity.ok(ApiResponse.success("success"));
    }
    
    // 차량 유형별 등록
    @PostMapping("/register")
    @Operation(summary = "차량 유형별 등록")
    public ResponseEntity<?> registerCar( @RequestBody @Valid VehicleRelatedRequest request) {
        Long vehicleId = cargateService.registerVehicle(request);

        return ResponseEntity.ok(ApiResponse.success(vehicleId));
    }

    // 세대 등록차량 조회
    @GetMapping("/registeredCar/list")
    @Operation(summary = "세대 등록차량 조회")
    public ResponseEntity<?> registeredCarList(){
        List<RegisCarResponse> registeredCars = cargateService.getRegisteredCars();
        return ResponseEntity.ok(ApiResponse.success(registeredCars));
    }

    // 세대 등록차량 상세정보 조회
    @GetMapping("/registeredCar/{vehicle_id}/detail")
    @Operation(summary = "세대 등록차량 상세정보 조회")
    public ResponseEntity<?> registeredCarDetail(@PathVariable("vehicle_id") Long vehicle_id){
        RegisCarDetailResponse regisCarDetail = cargateService.getRegisCarDetail(vehicle_id);
        return ResponseEntity.ok(ApiResponse.success(regisCarDetail));
    }

    // 세대 등록차량 정보수정 (일단 보류
    
    // 세대 등록차량 정보삭제
    @DeleteMapping("/registeredCar/delete")
    @Operation(summary = "세대 등록차량 정보삭제")
    public ResponseEntity<?> deleteByRegisteredCar(@RequestParam(name = "vehicle_id") Long vehicle_id) {
        if(!cargateService.deleteRegisCar(vehicle_id)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(ApiResponse.success(true));
    }

    // 관리자 승인차량 조회
    @GetMapping("/approvedCar/list")
    @Operation(summary = "관리자 승인차량 조회")
    public ResponseEntity<?> getApprovedCarList(){
        List<ApprovedCarResponse> approvededCarList = cargateService.ApprovedCarList();
        
        return ResponseEntity.ok(ApiResponse.success(approvededCarList));
    }
    

    // 관리자 승인차량 상세정보 조회
    @GetMapping("/approvedCar/{vehicle_id}/detail")
    @Operation(summary = "관리자 승인차량 상세정보 조회")
    public ResponseEntity<?> getApprovedCarDetail(@PathVariable("vehicle_id") Long vehicle_id){
        ApprovedCarDetailResponse approvedCarDetail = cargateService.getApprovedCarDetail(vehicle_id);
        return ResponseEntity.ok(ApiResponse.success(approvedCarDetail));
    }

    // 관리자 승인차량 수정
    @PutMapping("/approvedCar/{vehicle_id}/update")
    @Operation(summary = "관리자 승인차량 수정")
    public ResponseEntity<?> updateApprovedCar(@PathVariable("vehicle_id") Long vehicle_id, @RequestBody ApprovedCarRequest request) {
        cargateService.updateApprovedCar(vehicle_id, request);
        return ResponseEntity.ok(ApiResponse.success("수정완료"));
    }

    // 관리자 승인차량 삭제
    @DeleteMapping("/approvedCar/delete")
    @Operation(summary = "관리자 승인차량 삭제")
    public ResponseEntity<?> deleteByApprovedCar(@RequestParam(name = "vehicle_id") Long vehicle_id) {
        cargateService.deleteApprovedCar(vehicle_id);
        return ResponseEntity.ok(ApiResponse.success(true));
    }

}
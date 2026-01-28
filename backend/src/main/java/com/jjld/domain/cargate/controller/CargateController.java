package com.jjld.domain.cargate.controller;

import com.jjld.domain.cargate.dto.EntryExitRecordResponse;
import com.jjld.domain.cargate.dto.RecordDetailResponse;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.service.CargateService;
import com.jjld.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/cargate/api")
@RequiredArgsConstructor
public class CargateController {
    private final CargateService cargateService;

    @GetMapping("/lastweek")
    ResponseEntity<?> getLastWeekList() {
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 차량관리 페이지 요금 간단조회
    @GetMapping("/charge")
    ResponseEntity<?> getSimpleCharge(){
        return null;
    }

    // 백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회
    @GetMapping("/gateRecord/list")
    ResponseEntity<?> getNoticelist(
            @RequestParam(name = "size", defaultValue = "10")int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        Page<EntryExitRecordResponse> recordList = cargateService.getRecordList(size, page-1);
        return ResponseEntity.ok(ApiResponse.success(recordList));
    }

    // 방문차량 상세정보 조회
    @GetMapping("/detail")
    ResponseEntity<?> detailResponse(@RequestParam(name = "vehicle_id") Long vehicle_id) {
        RecordDetailResponse detailResponse = cargateService.getDetailInfo(vehicle_id);
        return ResponseEntity.ok(ApiResponse.success(detailResponse));
    }

    // 방문차량 상세정보 수정
    @PostMapping("/detail")
//    ResponseEntity<?> updateVehicleInfo(@RequestParam(name = "vehicle_id") Long vehicle_id, @RequestBody updateVehicleInfoRequest request){
//        return ResponseEntity.ok(ApiResponse.success());
//    }

    // 차량등록 요청
//    @PostMapping("/register")
//    ResponseEntity<?> createVehicleInfo(@RequestBody createVehicleRequest request){
//        return ResponseEntity.ok(ApiResponse.success());
//    }

    // 차량정보 삭제
    @DeleteMapping("/delete")
    ResponseEntity<?> deleteVehicleInfo(@RequestParam(name = "vehicle_id") Long vehicle_id){
        return ResponseEntity.ok(ApiResponse.success());
    }
}
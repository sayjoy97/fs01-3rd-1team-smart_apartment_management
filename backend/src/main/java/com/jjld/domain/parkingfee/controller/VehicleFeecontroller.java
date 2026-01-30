package com.jjld.domain.parkingfee.controller;

import com.jjld.domain.parkingfee.dto.SimpleRateResponse;
import com.jjld.domain.parkingfee.service.VehicleFeeService;
import com.jjld.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parkingfee/api")
@RequiredArgsConstructor
public class VehicleFeecontroller {
    private final VehicleFeeService vehicleFeeService;

     // 차량 출입관리 페이지 요금 간단조회 - 금일 누적금액&이번달 누적금액 조회
//    @GetMapping("/charge")
//    ResponseEntity<?> getSimpleCharge(){
//        SimpleRateResponse rateByType = vehicleFeeService.getRateByType();
//        return ResponseEntity.ok(ApiResponse.success(rateByType));
//    }

    // 요금내역관리 페이지 요금 통합조회
//    @GetMapping("/totalList")
//    ResponseEntity<?> getTotalList(){
//        return null;
//    }

    // 타입별 누적금액 조회
//    @GetMapping("/charge")
//    ResponseEntity<?> getCharge(@RequestParam(name = "type") int type){
//        return null;
//    }

    // 주차 요금 설정 모달창 기본내용 조회
//    @GetMapping("/charge/setting")
//    ResponseEntity<?> getChargeSetting(){
//        return null;
//    }

    // 주차 요금 설정 수정
//    @PostMapping("/charge/setting")
//    ResponseEntity<?> updateChargeSetting(@RequestBody updateChargeRequest request){
//        return null;
//    }
}

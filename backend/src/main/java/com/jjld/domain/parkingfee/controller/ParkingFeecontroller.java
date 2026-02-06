package com.jjld.domain.parkingfee.controller;

import com.jjld.domain.parkingfee.dto.*;
import com.jjld.domain.parkingfee.service.ParkingFeeService;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parkingfee/api")
@RequiredArgsConstructor
public class ParkingFeecontroller {
    private final ParkingFeeService vehicleFeeService;

     // 차량 출입관리 페이지 요금 간단조회 - 금일 누적금액&이번달 누적금액 조회
    @GetMapping("/charge")
    @Operation(summary = "차량 출입관리 페이지 요금 간단조회 - 금일 누적금액&이번달 누적금액 조회")
    ResponseEntity<?> getSimpleCharge(){
        SimpleRateResponse rateByType = vehicleFeeService.getRateByType();
        return ResponseEntity.ok(ApiResponse.success(rateByType));
    }

    // 요금내역관리 페이지 요금 통합조회
    @GetMapping("/totalList")
    @Operation(summary = "요금내역관리 페이지 요금 통합조회")
    ResponseEntity<?> getTotalList(){
        AllInOneChargeViewResponse allInOneChargeView = vehicleFeeService.getAllInOneChargeView();
        return ResponseEntity.ok(ApiResponse.success(allInOneChargeView));
    }

    // 최근 30일 일별 누적금액 조회
    @GetMapping("/runningTotal/daily")
    @Operation(summary = "최근 30일 일별 누적금액 조회")
    ResponseEntity<?> getDaily30TotalList(){
        List<Daily30TotalResponse> daily30Total = vehicleFeeService.getDaily30Total();
        return ResponseEntity.ok(ApiResponse.success(daily30Total));
    }

    // 최근 12개월 월별 누적금액 및 월별 평균 조회
    @GetMapping("/runningTotal/monthly")
    @Operation(summary = "최근 12개월 월별 누적금액 및 월별 평균 조회")
    ResponseEntity<?> getMonthlyTotalList(){
        List<MonthlyTotalResponse> monthlyTotal = vehicleFeeService.getMonthlyTotal();
        return ResponseEntity.ok(ApiResponse.success(monthlyTotal));
    }

    // 최근 3년 연간 누적금액 및 연간 평균 조회
    @GetMapping("/runningTotal/year")
    @Operation(summary = "최근 3년 연간 누적금액 및 연간 평균 조회")
    ResponseEntity<?> getYearTotalList(){
        List<YearTotalResponse> yearTotal = vehicleFeeService.getYearTotal();
        return ResponseEntity.ok(ApiResponse.success(yearTotal));
    }


    // 주차 요금 설정 모달창 기본내용 조회
    @GetMapping("/charge/setting")
    ResponseEntity<?> getChargeSetting(){
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 주차 요금 설정 수정
//    @PostMapping("/charge/setting")
//    ResponseEntity<?> updateChargeSetting(@RequestBody updateChargeRequest request){
//        return null;
//    }
}

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
@CrossOrigin(origins = "http://localhost:5173")
public class ParkingFeecontroller {
    private final ParkingFeeService parkingFeeService;

     // 차량 출입관리 페이지 요금 간단조회 - 금일 누적금액&이번달 누적금액 조회
    @GetMapping("/charge")
    @Operation(summary = "차량 출입관리 페이지 요금 간단조회 - 금일 누적금액&이번달 누적금액 조회")
    ResponseEntity<?> getSimpleCharge(){
        SimpleRateResponse rateByType = parkingFeeService.getRateByType();
        return ResponseEntity.ok(ApiResponse.success(rateByType));
    }

    // 요금내역관리 페이지 요금 통합조회
    @GetMapping("/totalList")
    @Operation(summary = "요금내역관리 페이지 요금 통합조회")
    ResponseEntity<?> getTotalList(){
        AllInOneChargeViewResponse allInOneChargeView = parkingFeeService.getAllInOneChargeView();
        return ResponseEntity.ok(ApiResponse.success(allInOneChargeView));
    }

    // 최근 30일 일별 누적금액 조회
    @GetMapping("/runningTotal/daily")
    @Operation(summary = "최근 30일 일별 누적금액 조회")
    ResponseEntity<?> getDaily30TotalList(){
        List<Daily30TotalResponse> daily30Total = parkingFeeService.getDaily30Total();
        return ResponseEntity.ok(ApiResponse.success(daily30Total));
    }

    // 최근 12개월 월별 누적금액 및 월별 평균 조회
    @GetMapping("/runningTotal/monthly")
    @Operation(summary = "최근 12개월 월별 누적금액 및 월별 평균 조회")
    ResponseEntity<?> getMonthlyTotalList(){
        List<MonthlyTotalResponse> monthlyTotal = parkingFeeService.getMonthlyTotal();
        return ResponseEntity.ok(ApiResponse.success(monthlyTotal));
    }

    // 최근 3년 연간 누적금액 및 연간 평균 조회
    @GetMapping("/runningTotal/year")
    @Operation(summary = "최근 3년 연간 누적금액 및 연간 평균 조회")
    ResponseEntity<?> getYearTotalList(){
        List<YearTotalResponse> yearTotal = parkingFeeService.getYearTotal();
        return ResponseEntity.ok(ApiResponse.success(yearTotal));
    }

    // 주차 요금 설정 모달창 기본내용 조회
    @GetMapping("/charge/setting")
    ResponseEntity<?> getChargeSetting(){
        FeeSettingResponse feeSetting = parkingFeeService.getFeeSetting();
        return ResponseEntity.ok(ApiResponse.success(feeSetting));
    }

    // 주차 요금 설정 수정
    @PostMapping("/charge/setting/update")
    ResponseEntity<?> updateChargeSetting(@RequestBody FeeSettingRequest request){
        parkingFeeService.createFeeSetting(request);
        return ResponseEntity.ok(ApiResponse.success("주차요금 수정 성공"));
    }
}

package com.jjld.domain.parkingfee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parkingfee/api")
public class ParkingFeecontroller {


    // 요금내역관리 페이지 요금 통합조회
    @GetMapping("/totalList")
    ResponseEntity<?> getTotalList(){
        return null;
    }

    // 타입별 누적금액 조회
    @GetMapping("/charge")
    ResponseEntity<?> getCharge(@RequestParam(name = "type") int type){
        return null;
    }

    // 주차 요금 설정 모달창 기본내용 조회
    @GetMapping("/charge/setting")
    ResponseEntity<?> getChargeSetting(){
        return null;
    }

    // 주차 요금 설정 수정
//    @PostMapping("/charge/setting")
//    ResponseEntity<?> updateChargeSetting(@RequestBody updateChargeRequest request){
//        return null;
//    }
}

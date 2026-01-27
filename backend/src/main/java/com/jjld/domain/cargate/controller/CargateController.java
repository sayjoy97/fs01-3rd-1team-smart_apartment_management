package com.jjld.domain.cargate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cargate/api")
public class CargateController {

    // 최근 7일 차량 출입현황 리스트 조회
    @GetMapping("/lastweek")
    ResponseEntity<?>  getLastWeekList() {
        return null;
    }

    // 차량관리 페이지 요금 간단조회
    @GetMapping("/charge")
    ResponseEntity<?> getSimpleCharge(){
        return null;
    }

    // 백엔드 페이지네이션을 이용한 차량출입기록 전체기록 조회
    @GetMapping("/gateRecord/list")
    ResponseEntity<?> getNoticelist(@RequestParam(name = "size")int size, @RequestParam(name = "page") int page){
        return null;
    }

    // 방문차량 상세정보 조회
    @GetMapping("/detail")
    ResponseEntity<?> detailResponse(@RequestParam(name = "vehicle_id") Long vehicle_id) {
        return null;
    }

    // 방문차량 상세정보 수정
    @PostMapping("/detail")
    ResponseEntity<?> updateVehicleInfo(@RequestParam(name = "vehicle_id") Long vehicle_id, @RequestBody updateVehicleInfoRequest request){
        return null;
    }

    // 차량등록 요청
    @PostMapping("/register")
    ResponseEntity<?> createVehicleInfo(@RequestBody createVehicleRequest request){
        return null;
    }

    // 차량정보 삭제
    @DeleteMapping("/delete")
    ResponseEntity<?> deleteVehicleInfo(@RequestParam(name = "vehicle_id") Long vehicle_id){
        return null;
    }
}
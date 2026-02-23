package com.jjld.domain.house.controller;


import com.jjld.domain.house.dto.*;
import com.jjld.domain.house.service.EntranceCardService;
import com.jjld.domain.house.service.HouseService;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class HouseController {
    private final HouseService service;
    private final EntranceCardService cardService;

    // 세대 목록 조회
    @GetMapping("/list")
    @Operation(summary = "세대 목록 조회")
    public ResponseEntity<?> search(HouseSearchCond cond){
        List<HouseResponse> houseList = service.search(cond);

        return ResponseEntity.ok(
                ApiResponse.success(houseList)
        );
    }

    // 세대 상세 조회
    @GetMapping("/detail/{houseId}")
    @Operation(summary = "세대 상세 조회")
    public ResponseEntity<?> getDetail(@PathVariable Long houseId){

        HouseDetailResponse res = service.getDetail(houseId);

        return ResponseEntity.ok(
                ApiResponse.success(res)
        );
    }

    // 세대 관리 (등록/수정/초기화)
    @PutMapping("/insert")
    @Operation(summary = "세대 관리 (등록/수정/초기화)")
    public ResponseEntity<?> insertHouse(
            @RequestParam Long houseId,
            @RequestBody HouseManagementResponse houseInsert
            ){
        service.houseInsert(houseId, houseInsert);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

    // 세대 카드 목록 조회
    @GetMapping("/card/list")
    @Operation(summary = "세대 카드 조회")
    public ResponseEntity<?> entranceCardList(){
        List<EntranceCardResponse> getCardList = cardService.findAll();

        return ResponseEntity.ok(
                ApiResponse.success(getCardList)
        );
    }

}

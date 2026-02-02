package com.jjld.domain.house.controller;


import com.jjld.domain.house.dto.HouseManagementResponse;
import com.jjld.domain.house.dto.HouseResponse;
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
public class HouseController {
    private final HouseService service;

    // 세대 목록 조회
    @GetMapping("/list")
    @Operation(summary = "세대 목록 조회")
    public ResponseEntity<?> getHouseList(){
        List<HouseResponse> houseList = service.findAll();

        return ResponseEntity.ok(
                ApiResponse.success(houseList)
        );
    }

    // 세대 등록
    @PutMapping("/insert")
    @Operation(summary = "세대 등록")
    public ResponseEntity<?> insertHouse(
            @RequestParam Long houseId,
            @RequestBody HouseManagementResponse houseInsert
            ){
        service.houseInsert(houseId, houseInsert);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

}

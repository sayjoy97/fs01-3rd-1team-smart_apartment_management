package com.jjld.domain.house.controller;


import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.service.HouseService;
import com.jjld.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/house/api")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService service;

    // 세대 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getHouseList(){
        List<HouseResponse> houseList = service.findAll();

        return ResponseEntity.ok(
                ApiResponse.success(houseList)
        );
    }
}

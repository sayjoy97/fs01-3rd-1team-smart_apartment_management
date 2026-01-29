package com.jjld.domain.complex.controller;

import com.jjld.domain.complex.dto.ComplexReq;
import com.jjld.domain.complex.service.ComplexService;
import com.jjld.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/complex/api")
@RequiredArgsConstructor
public class ComplexController {
    private final ComplexService complexService;

//    // 단지 정보 생성
//    @PostMapping()
//    public ResponseEntity<?> createComplex(@Valid @RequestBody ComplexReq complexReq) {
//        complexService.createComplex(complexReq);
//        return ResponseEntity.ok(ApiResponse.success("단지 정보 생성을 성공했습니다."));
//    }
}

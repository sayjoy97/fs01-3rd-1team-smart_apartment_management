package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dto.GardenReq;
import com.jjld.domain.garden.dto.GardenRes;
import com.jjld.domain.garden.entity.Garden;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GardenServiceImpl implements GardenService {
    private final GardenDAO gardenDAO;
    private final ModelMapper modelMapper;

    // 정원 관리 구역 생성
    @Override
    public void createGarden(GardenReq gardenReq) {
        if (gardenReq.getName().equals("") || gardenReq.getName() == null) {
            gardenReq.setName(gardenReq.getLocation());
        }
        Garden garden = modelMapper.map(gardenReq, Garden.class);
        gardenDAO.createGarden(garden);
    }

    // 정원 관리 구역 목록 조회
    @Override
    public List<GardenRes> getGardens() {
        List<Garden> Gardens = gardenDAO.getGardens();
        List<GardenRes> response = Gardens
                .stream()
                .map(Garden -> modelMapper.map(Garden, GardenRes.class))
                .collect(Collectors.toList());
        return response;
    }
}

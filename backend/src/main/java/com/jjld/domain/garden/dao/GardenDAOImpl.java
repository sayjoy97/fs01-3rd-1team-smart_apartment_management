package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.repository.GardenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GardenDAOImpl implements GardenDAO {
    private final GardenRepository gardenRepository;

    // 정원 관리 구역 생성
    @Override
    public void createGarden(Garden garden) {
        gardenRepository.save(garden);
    }

    // 정원 관리 구역 목록 조회
    @Override
    public List<Garden> getGardens() {
        return gardenRepository.findAll();
    }
}

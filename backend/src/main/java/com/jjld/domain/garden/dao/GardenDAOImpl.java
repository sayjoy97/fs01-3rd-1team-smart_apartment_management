package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.repository.GardenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    // gardenId를 활용해 조회
    @Override
    public Optional<Garden> getGarden(Long gardenId) {
        return gardenRepository.findById(gardenId);
    }

    // 정원 관리 구역 수정
    @Override
    public void updateGarden(Garden garden) {
        gardenRepository.save(garden);
    }

    // 정원 관리 구역 삭제
    @Override
    public void deleteGarden(Long gardenId) {
        gardenRepository.deleteById(gardenId);
    }
}

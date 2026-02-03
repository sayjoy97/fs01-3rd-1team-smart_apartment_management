package com.jjld.domain.house.service;

import com.jjld.domain.house.dao.HouseDAO;
import com.jjld.domain.house.dto.HouseResponse;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.HouseRepository;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService{
    private final HouseRepository houseRepository;
    private final HouseDAO houseDAO;

    // 세대 목록 조회
    @Override
    public List<HouseResponse> findAll() {
        List<House> houseList = houseRepository.findAll();

        return houseList.stream()
                .map(h -> new HouseResponse(
                        h.getHouseId(),
                        h.getHouseDong(),
                        h.getHouseHo(),
                        h.getHouseholderName(),
                        h.getHouseholderPhone(),
                        h.getMoveInAt(),
                        h.getHouseStatus()
                ))
                .toList();
    }

    @Override
    public HouseResponse findByIdHouseId(Long houseId) {
        House house = houseDAO.findHouseId(houseId);
        if(houseId == null){
            throw new NotFoundException(ErrorCode.HOUSE_NOT_FOUND, "없는 세대 번호입니다");
        }

        HouseResponse houseResponse = HouseResponse.builder()
                .houseId(house.getHouseId())
                .houseDong(house.getHouseDong())
                .build();
        return null;
    }


}

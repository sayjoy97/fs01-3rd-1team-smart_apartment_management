package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.GardenReq;
import com.jjld.domain.garden.dto.GardenRes;
import com.jjld.domain.garden.dto.ScheduleFilterRes;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GardenService {
    void createGarden(GardenReq gardenReq);

    List<GardenRes> getGardens();

    void updateGarden(Long gardenId, GardenReq gardenReq);

    void deleteGarden(Long gardenId, Long adminId);

    void toggleWatering(Long gardenId);

    Page<ScheduleFilterRes> getGardenDetail(Long gardenId);
}

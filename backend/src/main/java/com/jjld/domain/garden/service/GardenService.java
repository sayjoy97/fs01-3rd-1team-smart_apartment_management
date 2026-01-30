package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.GardenReq;
import com.jjld.domain.garden.dto.GardenRes;

import java.util.List;

public interface GardenService {
    void createGarden(GardenReq gardenReq);

    List<GardenRes> getGardens();
}

package com.jjld.domain.elevator.service;

import com.jjld.domain.elevator.dto.AdvertisementReq;

public interface AdvertisementService {
    void createAdvertisement(Long adminId, AdvertisementReq advertisementReq);
}

package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdvertisementDAOImpl implements AdvertisementDAO {
    private final AdvertisementRepository advertisementRepository;
}

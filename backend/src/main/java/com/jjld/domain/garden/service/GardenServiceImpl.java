package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dao.GardenDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GardenServiceImpl implements GardenService {
    private final GardenDAO gardenDAO;
}

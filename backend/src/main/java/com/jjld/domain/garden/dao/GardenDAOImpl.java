package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.repository.GardenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GardenDAOImpl implements GardenDAO {
    private final GardenRepository gardenRepository;
}

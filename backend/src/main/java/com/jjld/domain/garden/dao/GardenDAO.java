package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Garden;

import java.util.List;
import java.util.Optional;

public interface GardenDAO {
    void createGarden(Garden garden);

    List<Garden> getGardens();

    Optional<Garden> getGarden(Long gardenId);

    void updateGarden(Garden garden);

    void deleteGarden(Long gardenId);
}

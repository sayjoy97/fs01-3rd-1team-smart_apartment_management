package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Garden;

import java.util.List;

public interface GardenDAO {
    void createGarden(Garden garden);

    List<Garden> getGardens();
}

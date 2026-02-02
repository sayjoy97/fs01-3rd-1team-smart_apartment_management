package com.jjld.domain.garden.repository;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Garden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findAllByGarden(Garden garden);
}

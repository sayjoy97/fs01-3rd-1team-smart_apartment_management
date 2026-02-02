package com.jjld.domain.garden.repository;

import com.jjld.domain.garden.entity.Garden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GardenRepository extends JpaRepository<Garden, Long> {
}

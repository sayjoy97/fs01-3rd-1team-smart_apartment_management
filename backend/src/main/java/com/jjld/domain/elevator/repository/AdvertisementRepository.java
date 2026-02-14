package com.jjld.domain.elevator.repository;

import com.jjld.domain.elevator.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<Advertisement,Long> {
}

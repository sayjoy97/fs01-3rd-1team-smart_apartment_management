package com.jjld.domain.entrancedoor.repository;

import com.jjld.domain.entrancedoor.entity.EntranceDoor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntranceDoorRepository extends JpaRepository<EntranceDoor, Long> {
    // 동 번호로 공동 현관 조회
    Optional<EntranceDoor> findByHouseDong(Integer houseDong);
}

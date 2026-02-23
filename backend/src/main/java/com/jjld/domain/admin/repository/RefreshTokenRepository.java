package com.jjld.domain.admin.repository;

import com.jjld.domain.admin.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByAdminId(Long adminId);

    Optional<RefreshToken> findByAdminId(Long adminId);
}

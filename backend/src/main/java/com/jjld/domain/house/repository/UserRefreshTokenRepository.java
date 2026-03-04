package com.jjld.domain.house.repository;

import com.jjld.domain.house.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    // 토큰 문자열로 조회
    Optional<UserRefreshToken> findByToken(String token);

    // 유저 기준으로 삭제 (로그인 중복 방지)
    void deleteByAccountId(Long accountId);
}

package com.chachef.repository;


import com.chachef.entity.RefreshTokens;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshTokens, UUID> {
    @Transactional
    void deleteByExpiresBefore(LocalDateTime time);
    @Transactional
    void deleteRefreshTokensByRefreshId(UUID refreshTokenId);
    @Transactional
    void deleteRefreshTokensByUser_UserId(UUID user_UserId);
    Optional<RefreshTokens> findRefreshTokensByRefreshId(UUID refreshId);
}

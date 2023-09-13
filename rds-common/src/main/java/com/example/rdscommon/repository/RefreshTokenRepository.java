package com.example.rdscommon.repository;

import com.example.rdscommon.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByJti(String jti);
    Optional<RefreshToken> findByJti(String jti);
    void deleteByUserIdAndIpAndOsAndBrowser(String userId, String ip, String os, String browser);
    void deleteByJti(String jti);
}

package com.JVM.eCart.auth.service;

import com.JVM.eCart.auth.entity.RefreshToken;
import com.JVM.eCart.auth.repository.RefreshTokenRepository;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshToken.expirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public String createRefreshToken(Long userId) {
        RefreshToken token = refreshTokenRepository.findByUser_Id(userId)
                .orElse(new RefreshToken());

        token.setUser(userRepository.findById(userId).get());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        token.setToken(UUID.randomUUID().toString());

        refreshTokenRepository.save(token);

        return token.getToken();
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired, Please login again");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

}

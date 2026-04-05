package com.JVM.eCart.auth.repository;

import com.JVM.eCart.auth.entity.ForgotPasswordToken;
import com.JVM.eCart.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {

    Optional<ForgotPasswordToken> findByToken(String token);

    Optional<ForgotPasswordToken> findByUser(User user);

    void deleteByUser(User user);
}

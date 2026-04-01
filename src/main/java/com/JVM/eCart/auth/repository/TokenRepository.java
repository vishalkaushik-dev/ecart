package com.JVM.eCart.auth.repository;

import com.JVM.eCart.auth.entity.User;
import com.JVM.eCart.auth.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<VerificationToken,Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUser(User user);

    Optional<VerificationToken> findByUser(User user);

}

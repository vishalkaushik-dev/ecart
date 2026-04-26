package com.JVM.eCart.audit;

import com.JVM.eCart.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@AllArgsConstructor
public class AuditorConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(getCurrentUser());
    }

    private String getCurrentUser() {
        // Example: fetch from Spring Security
        return SecurityContextHolder.getContext().getAuthentication().getName(); // replace with actual logic
    }

}

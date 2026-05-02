package com.JVM.eCart.audit;

import com.JVM.eCart.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return "system";
        }

        String username = auth.getName();
        return (username == null || username.equals("anonymousUser")) ? "system" : username;

    }

}

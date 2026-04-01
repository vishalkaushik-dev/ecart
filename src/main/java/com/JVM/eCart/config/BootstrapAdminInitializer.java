package com.JVM.eCart.config;

import com.JVM.eCart.auth.entity.Role;
import com.JVM.eCart.auth.entity.User;
import com.JVM.eCart.auth.repository.RoleRepository;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class BootstrapAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String ...args) {

        String adminEmail = "admin@gmail.com";
        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

        if(existingAdmin.isEmpty()) {

            Role adminRole = roleRepository.findByAuthority("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setActive(true);
            admin.setBootstrapAdmin(true);

            admin.getRoles().add(adminRole);

            userRepository.save(admin);

            System.out.println("Bootstrap Admin Created");
        }

    }
}

package com.JVM.eCart.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String password;

    private String firstName;

    private String lastName;

    private boolean isActive = false;

    private boolean isLocked = false;

    private boolean isExpired = false;

    private Integer invalidAttemptCount = 0;

    @Column(name = "is_bootstrap_admin", nullable = false)
    private boolean isBootstrapAdmin = false;

    private LocalDateTime passwordUpdateDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

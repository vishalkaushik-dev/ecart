package com.JVM.eCart.user.entity;

import com.JVM.eCart.audit.Auditable;
import com.JVM.eCart.auth.entity.Role;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.seller.entity.Seller;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User extends Auditable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Pattern(
            regexp = "^[A-Za-z]+( [A-Za-z]+)*$",
            message = "Name must contain only letters and single spaces between words"
    )
    private String firstName;

    @Pattern(
            regexp = "^[A-Za-z]+( [A-Za-z]+)*$",
            message = "Name must contain only letters and single spaces between words"
    )
    private String lastName;

    @Column(unique = true)
    private String phoneNumber;

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Seller seller;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
}

package com.JVM.eCart.user.entity;

import com.JVM.eCart.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK column
    private User user;

}

package com.JVM.eCart.customer.entity;

import com.JVM.eCart.auth.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String phoneNumber;
    private String addressLine1;
    private String city;
    private String state;
    private String pincode;
}

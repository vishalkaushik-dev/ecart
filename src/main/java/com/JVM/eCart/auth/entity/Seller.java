package com.JVM.eCart.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String gst;
    private String companyContact;
    private String companyName;
    private String companyAddress;
}
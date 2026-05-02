package com.JVM.eCart.seller.entity;

import com.JVM.eCart.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true)
    private String gst;

    @Column(unique = true)
    private String companyContact;

    @Column(unique = true)
    private String companyName;

    private String companyAddress;
}
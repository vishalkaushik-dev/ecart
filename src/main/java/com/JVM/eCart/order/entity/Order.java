package com.JVM.eCart.order.entity;

import com.JVM.eCart.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many order has one customer
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "amountPaid", nullable = false)
    private BigDecimal amountPaid;

    private LocalDateTime dateCreated;

    private String paymentMethod;

    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
    private String label;
}

package com.JVM.eCart.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variation")
@Data
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Many variations belong to one product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantityAvailable;

    private BigDecimal price;

    // JSON field
    @Column(columnDefinition = "json")
    private String metadata;

    private String primaryImageName;

    private Boolean isActive = true;
}
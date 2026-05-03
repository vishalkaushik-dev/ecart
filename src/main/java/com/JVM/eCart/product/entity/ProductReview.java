package com.JVM.eCart.product.entity;

import com.JVM.eCart.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_review")
@Data
public class ProductReview extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerUserId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String review;

    private Integer rating; // 1–5
}
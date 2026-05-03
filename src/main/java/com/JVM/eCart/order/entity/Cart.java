package com.JVM.eCart.order.entity;

import com.JVM.eCart.audit.Auditable;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.product.entity.ProductVariation;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(
        name = "cart",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"customer_id", "product_variation_id"}
        )
)
public class Cart extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    @Column(nullable = false)
    private Integer quantity;

    private Boolean isWishItem = false;
}

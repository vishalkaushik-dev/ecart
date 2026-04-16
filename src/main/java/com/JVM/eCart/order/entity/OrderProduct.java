package com.JVM.eCart.order.entity;

import com.JVM.eCart.product.entity.ProductVariation;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_product")
@Data
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Integer quantity;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY) // Many product items has one order
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    @OneToMany(fetch = FetchType.LAZY)
    List<OrderStatus> orderStatusList;
}

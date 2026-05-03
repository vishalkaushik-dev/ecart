package com.JVM.eCart.product.entity;

import com.JVM.eCart.audit.Auditable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "product_variation")
@Data
public class ProductVariation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Many variations belong to one product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantityAvailable;

    private BigDecimal price;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> metadata;

    private String primaryImage;

    private List<String> secondaryImages;

    private Boolean isActive = true;
}
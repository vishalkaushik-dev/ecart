package com.JVM.eCart.product.entity;

import com.JVM.eCart.audit.Auditable;
import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.seller.entity.Seller;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(
        name = "product",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"name", "brand", "category_id", "seller_user_id"}
        )
)
@Data
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // MUST be leaf

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductVariation> variations;

    private Boolean isCancellable;
    private Boolean isReturnable;

    private String brand;

    private Boolean isActive = true;
    private Boolean isDeleted = false;
}

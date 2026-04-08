package com.JVM.eCart.category.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "category_metadata_field_values",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"category_id", "category_metadata_field_id", "value"}
        )
)
@Data
public class CategoryMetadataFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String values; // M, L ,XL,

    @ManyToOne
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetadataField metadataField;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

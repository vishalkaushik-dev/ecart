package com.JVM.eCart.category.entity;

import com.JVM.eCart.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(
        name = "category_metadata_field_values",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"category_id", "category_metadata_field_id", "values"}
        )
)
@Data
public class CategoryMetadataFieldValue extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(
            regexp = "^[A-Za-z]+(,[A-Za-z]+)*$",
            message = "Values must be comma-separated without spaces"
    )
    private String values; // M, L ,XL,

    @ManyToOne
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetadataField metadataField;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

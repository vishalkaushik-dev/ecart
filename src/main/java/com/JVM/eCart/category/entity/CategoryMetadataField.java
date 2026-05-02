package com.JVM.eCart.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "category_metadata_field")
@Data
public class CategoryMetadataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    @Pattern(
            regexp = "^[A-Za-z]+( [A-Za-z]+)*$",
            message = "Name must contain only letters and single spaces between words"
    )
    private String name; // size, colour, length
}

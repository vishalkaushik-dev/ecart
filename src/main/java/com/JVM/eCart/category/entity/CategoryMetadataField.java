package com.JVM.eCart.category.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category_metadata_field")
@Data
public class CategoryMetadataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    private String name; // size, colour, length
}

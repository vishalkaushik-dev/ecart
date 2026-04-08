package com.JVM.eCart.category.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name; // shirt, shoes

    // Self referencing for parent category
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
}

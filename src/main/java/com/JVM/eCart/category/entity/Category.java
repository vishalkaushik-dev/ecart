package com.JVM.eCart.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Pattern(
            regexp = "^[A-Za-z]+( [A-Za-z]+)*$",
            message = "Name must contain only letters and single spaces between words"
    )
    private String name; // shirt, shoes

    // Self referencing for parent category
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
}

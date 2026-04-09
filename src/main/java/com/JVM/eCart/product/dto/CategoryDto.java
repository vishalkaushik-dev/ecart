package com.JVM.eCart.product.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class CategoryDto {

    private Long id;
    private String name;
    private CategoryDto parent;
}

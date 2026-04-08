package com.JVM.eCart.category.dto;

import lombok.Data;

@Data
public class CategoryFilterRequest {

    private Integer max = 10;     // limit
    private Integer offset = 0;   // pagination

    private String sort = "id";   // id/field
    private String order = "asc"; // asc/desc

    private String query;         // search by name
}

package com.JVM.eCart.category.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FetchCategoryResponse(

        Long id,
        String name,
        List<ParentCategoryDto> parent,
        List<FetchCategoryResponse> children,
        List<CategoryMetadataResponse> metadata
) { }

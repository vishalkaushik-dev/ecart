package com.JVM.eCart.category.dto;

import java.util.List;

public record CategoryMetadataResponse(
        String field,
        List<String> values
) { }

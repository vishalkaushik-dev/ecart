package com.JVM.eCart.category.repository;

import com.JVM.eCart.category.entity.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryMetaDataFieldRepository extends JpaRepository<CategoryMetadataField, Long> {

    boolean existsByNameIgnoreCase(String name);

    Page<CategoryMetadataField> findByNameIgnoreCase(String name, Pageable pageable);

}

package com.JVM.eCart.category.repository;

import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.category.entity.CategoryMetadataField;
import com.JVM.eCart.category.entity.CategoryMetadataFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValue, Long> {

    List<CategoryMetadataFieldValue> findByCategory_Id(Long categoryId);

    boolean existsByCategory_IdAndMetadataField_IdAndValuesIgnoreCase(Long categoryId, Long fieldId, String value);

    boolean existsByCategoryAndMetadataField(Category category, CategoryMetadataField field);

    Optional<CategoryMetadataFieldValue> findByCategoryAndMetadataField(Category category, CategoryMetadataField field);
}
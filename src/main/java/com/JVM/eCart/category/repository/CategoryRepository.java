package com.JVM.eCart.category.repository;

import com.JVM.eCart.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByNameAndParentCategory_Id(String name, Long parentId);

    List<Category> findByParentCategory_Id(Long parentId);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCaseAndParentCategory_IdAndIdNot(String name, Long parentId, Long id);

    // Leaf nodes = categories that are NOT parent of any category
    @Query("""
        SELECT c FROM Category c
        WHERE c.id NOT IN (
            SELECT DISTINCT c2.parentCategory.id FROM Category c2 WHERE c2.parentCategory IS NOT NULL
        )
    """)
    List<Category> findLeafCategories();

    List<Category> findByParentCategoryIsNull(); // root Categories

    List<Category> findByParentCategoryId(Long parentId); // children

    Boolean existsByParentCategory_Id(Long categoryId);

}

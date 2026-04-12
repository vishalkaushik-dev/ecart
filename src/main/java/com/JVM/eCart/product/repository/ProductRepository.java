package com.JVM.eCart.product.repository;

import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIn(List<Category> categories);

    boolean existsByNameAndBrandAndCategory_IdAndSellerUserId(String name, String brand, Long categoryId, Long sellerUserId);

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Page<Product> findBySeller_User_IdAndIsDeletedFalse(Long sellerUserId, Pageable pageable);

    Page<Product> findByNameIgnoreCaseAndSeller_User_IdAndIsDeletedFalse(String name, Long sellerUserId, Pageable pageable);

    boolean existsByNameAndBrandAndCategory_IdAndSeller_User_IdAndIsDeletedFalse(String name, String brand, Long categoryId, Long sellerUserId);

    @Query("""
                SELECT DISTINCT p FROM Product p
                join p.variations v
                WHERE p.category.id = :categoryId
                AND p.isDeleted = false
                AND p.isActive = true
                AND v.isActive = true
                AND (:query IS NULL OR :query = '' OR LOWER(p.name) LIKE CONCAT('%', LOWER(:query), '%'))
            """)
    Page<Product> findAllProducts(Long categoryId, String query, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            where p.category.id = :categoryId
            AND p.id != :productId
            AND p.isDeleted = false
            AND p.isActive = true
            AND EXISTS(
                SELECT 1 FROM ProductVariation v
                WHERE v.product = p AND v.isActive = true
            )
            AND (:brand IS NULL OR p.brand = :brand)
            AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Product> findSimilarProducts(@Param("categoryId") Long categoryId, @Param("productId") Long productId, @Param("brand") String brand, @Param("query") String query, Pageable pageable);

    @Query("""
                SELECT p FROM Product p
                WHERE p.isDeleted = false
                AND p.isActive = true
                AND (:categoryId IS NULL OR p.category.id = :categoryId)
                AND (:sellerId IS NULL OR p.seller.id = :sellerId)
            """)
    Page<Product> findAllProductsWithQuery(@Param("categoryId") Long categoryId, @Param("sellerId") Long sellerId, Pageable pageable);
}

package com.JVM.eCart.product.repository;

import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.entity.ProductVariation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

    List<ProductVariation> findByProductIn(List<Product> products);

    List<ProductVariation> findByProduct_Id(Long productId);

    @Query("""
    SELECT pv FROM ProductVariation pv
    JOIN pv.product p
    WHERE p.seller.user.id = :sellerUserId
    AND p.isDeleted = false
""")
    Page<ProductVariation> findAllBySellerUserId(@Param("sellerUserId") Long sellerUserId, Pageable pageable);

    @Query("""
    SELECT pv FROM ProductVariation pv
    JOIN pv.product p
    WHERE pv.id = :productVariationId
     AND p.seller.user.id = :sellerUserId
    AND p.isDeleted = false
""")
    Page<ProductVariation> findAllByIdAndSellerUserId(@Param("sellerUserId") Long sellerUserId, @Param("productVariationId") Long productVariationId, Pageable pageable);

    List<ProductVariation> findByProduct_IdAndIsActiveTrue(Long productId);

}

package com.JVM.eCart.product.repository;

import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

    List<ProductVariation> findByProductIn(List<Product> products);

}

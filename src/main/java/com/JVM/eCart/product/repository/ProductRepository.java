package com.JVM.eCart.product.repository;

import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIn(List<Category> categories);

    boolean existsByNameAndBrandAndCategory_IdAndSellerUserId(String name, String brand, Long categoryId, Long sellerUserId);

}

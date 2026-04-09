package com.JVM.eCart.product.repository;

import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIn(List<Category> categories);

    boolean existsByNameAndBrandAndCategory_IdAndSellerUserId(String name, String brand, Long categoryId, Long sellerUserId);

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Page<Product>  findBySeller_User_IdAndIsDeletedFalse(Long sellerUserId, Pageable pageable);


}

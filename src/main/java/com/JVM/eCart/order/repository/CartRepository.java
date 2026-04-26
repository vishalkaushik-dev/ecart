package com.JVM.eCart.order.repository;

import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.order.entity.Cart;
import com.JVM.eCart.product.entity.ProductVariation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerAndProductVariation(Customer customer, ProductVariation productVariation);

    List<Cart> findByCustomer(Customer customer);

    Optional<Cart> findByCustomer_User_IdAndProductVariation_Id(Long userId, Long productVariationId);

    List<Cart> findByCustomer_User_IdAndProductVariation_IdIn(Long userId, List<Long> productVariationIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.customer.user.id = :userId")
    int deleteByCustomer_User_Id(@Param("userId") Long userId);
}

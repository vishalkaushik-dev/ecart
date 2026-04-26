package com.JVM.eCart.order.repository;

import com.JVM.eCart.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomer_User_Id(Long userId, Pageable pageable);

    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN OrderProduct op ON op.order = o
    JOIN ProductVariation pv ON op.productVariation = pv
    JOIN Product p ON pv.product = p
    WHERE p.seller.user.id = :userId
    """)
    Page<Order> findOrdersBySellerUserId(Long userId, Pageable pageable);
}

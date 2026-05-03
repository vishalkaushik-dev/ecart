package com.JVM.eCart.order.repository;

import com.JVM.eCart.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByCustomer_User_Id(Long userId, Pageable pageable);

    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN OrderProduct op ON op.order = o
    JOIN ProductVariation pv ON op.productVariation = pv
    JOIN Product p ON pv.product = p
    WHERE p.seller.user.id = :userId
    """)
    Page<Order> findOrdersBySellerUserId(Long userId, Pageable pageable);

    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN o.customer c
    JOIN c.user u
    JOIN OrderProduct op ON op.order = o
    JOIN ProductVariation pv ON op.productVariation = pv
    JOIN Product p ON pv.product = p
    WHERE
        (:query IS NULL OR
         LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR
         LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
         LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR
         CAST(o.id AS string) LIKE CONCAT('%', :query, '%'))
    """)
    Page<Order> searchOrders(String query, Pageable pageable);
}

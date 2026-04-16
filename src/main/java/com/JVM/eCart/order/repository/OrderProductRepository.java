package com.JVM.eCart.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<com.JVM.eCart.order.entity.OrderProduct, Long> {
}

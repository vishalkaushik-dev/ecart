package com.JVM.eCart.order.repository;

import com.JVM.eCart.order.entity.OrderProduct;
import com.JVM.eCart.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    Optional<OrderStatus> findTopByOrderProductOrderByTransitionDateDesc(OrderProduct orderProduct);
}

package com.JVM.eCart.order.repository;

import com.JVM.eCart.order.entity.Order;
import com.JVM.eCart.order.entity.OrderProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<com.JVM.eCart.order.entity.OrderProduct, Long> {

    List<OrderProduct> findByOrder(Order order);
}

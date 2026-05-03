package com.JVM.eCart.order.specification;

import com.JVM.eCart.order.entity.Order;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> search(String query) {
        return (root, cq, cb) -> {

            if (query == null || query.isBlank()) {
                return cb.conjunction();
            }

            // joins
            Join<Object, Object> customer = root.join("customer");
            Join<Object, Object> user = customer.join("user");
            Join<Object, Object> orderProduct = root.join("orderProducts");
            Join<Object, Object> variation = orderProduct.join("productVariation");
            Join<Object, Object> product = variation.join("product");

            String likeQuery = "%" + query.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(user.get("email")), likeQuery),
                    cb.like(cb.lower(product.get("name")), likeQuery),
                    cb.like(cb.lower(product.get("brand")), likeQuery),
                    cb.like(cb.lower(root.get("id").as(String.class)), likeQuery)
            );
        };
    }
}

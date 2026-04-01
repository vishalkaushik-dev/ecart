package com.JVM.eCart.auth.repository;

import com.JVM.eCart.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


}

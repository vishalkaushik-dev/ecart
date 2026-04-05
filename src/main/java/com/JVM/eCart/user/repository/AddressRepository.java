package com.JVM.eCart.user.repository;

import com.JVM.eCart.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

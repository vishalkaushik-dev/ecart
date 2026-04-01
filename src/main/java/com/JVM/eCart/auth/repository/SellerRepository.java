package com.JVM.eCart.auth.repository;

import com.JVM.eCart.auth.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByGst(String gst);

    boolean existsByCompanyNameIgnoreCase(String companyName);
}

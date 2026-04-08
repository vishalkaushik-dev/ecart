package com.JVM.eCart.seller.repository;

import com.JVM.eCart.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByGst(String gst);

    boolean existsByCompanyNameIgnoreCase(String companyName);

    Optional<Seller> findByUser_Id(Long userId);
}

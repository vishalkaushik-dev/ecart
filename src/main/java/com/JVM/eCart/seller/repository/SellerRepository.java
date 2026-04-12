package com.JVM.eCart.seller.repository;

import com.JVM.eCart.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByGst(String gst);

    boolean existsByCompanyNameIgnoreCase(String companyName);

    Optional<Seller> findByUser_Id(Long userId);
}

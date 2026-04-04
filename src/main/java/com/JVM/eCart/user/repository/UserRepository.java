package com.JVM.eCart.user.repository;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.admin.dto.RegisteredSellerResponse;
import com.JVM.eCart.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
    SELECT new com.JVM.eCart.admin.dto.RegisteredCustomerResponse(
        u.id,
        (u.firstName || ' ' || u.lastName),
        u.email,
        u.isActive
    )
    FROM User u
    JOIN u.roles r
    WHERE LOWER(r.authority) = LOWER(:role)
    AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))
""")
    Page<RegisteredCustomerResponse> findRegisteredCustomers(
            @Param("role") String role,
            @Param("email") String email,
            Pageable pageable
    );

    @Query(
            """
                    SELECT new com.JVM.eCart.admin.dto.RegisteredSellerResponse(
                        u.id,
                        CONCAT(u.firstName, ' ', u.lastName),
                        u.email,
                        u.isActive,
                        s.companyName,
                        s.companyAddress,
                        s.companyContact
                    ) FROM User u
                    JOIN u.roles r
                    JOIN Seller s ON s.user = u
                    WHERE LOWER(r.authority) = LOWER(:role)
                    AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))
            """
    )
    Page<RegisteredSellerResponse> findRegisteredSellers(
            @Param("role") String role,
            @Param("email") String email,
            Pageable pageable
    );
}

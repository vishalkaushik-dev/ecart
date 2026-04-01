package com.JVM.eCart.user.repository;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
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
        CONCAT(u.firstName, ' ', u.lastName),
        u.email,
        u.isActive
    )
    FROM User u
    WHERE (:email IS NULL OR u.email LIKE CONCAT('%', :email, '%'))
    """)
    Page<RegisteredCustomerResponse> findRegisteredCustomers(@Param("email") String email, Pageable pageable);
}

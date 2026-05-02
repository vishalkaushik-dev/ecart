package com.JVM.eCart.user.repository;

import com.JVM.eCart.user.entity.Address;
import com.JVM.eCart.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Boolean existsByUserAndAddressLineIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCaseAndZipCodeIgnoreCase(
            User user,
            String addressLine,
            String city,
            String state,
            String country,
            String zipCode
    );
}

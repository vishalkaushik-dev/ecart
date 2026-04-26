package com.JVM.eCart.customer.service;

import com.JVM.eCart.auth.service.AuthServiceImpl;
import com.JVM.eCart.customer.dto.CustomerViewProfileResponse;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerService {

    private final AuthServiceImpl authService;

    public CustomerViewProfileResponse viewProfile() {

        User user = authService.getLoggedInUser();
        List<AddressDto> addressDtoList = user.getAddresses().stream()
                .map(addr -> new AddressDto(
                        addr.getId(),
                        addr.getAddressLine(),
                        addr.getCity(),
                        addr.getState(),
                        addr.getCountry(),
                        addr.getLabel(),
                        addr.getZipCode()
                ))
                .toList();

        return new CustomerViewProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                user.getPhoneNumber(),
                addressDtoList
        );
    }

    public Map<String,List<AddressDto>> getAddresses() {
        User user = authService.getLoggedInUser();
        List<AddressDto> list = user.getAddresses().stream()
                .map(addr -> new AddressDto(
                        addr.getId(),
                        addr.getAddressLine(),
                        addr.getCity(),
                        addr.getState(),
                        addr.getCountry(),
                        addr.getLabel(),
                        addr.getZipCode()
                ))
                .toList();

        return Map.of("addresses",list);
    }
}

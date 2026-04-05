package com.JVM.eCart.user.service;

import com.JVM.eCart.auth.service.AuthServiceImpl;
import com.JVM.eCart.auth.service.EmailService;
import com.JVM.eCart.customer.dto.AddAddressRequest;
import com.JVM.eCart.customer.dto.UpdateCustomerProfileRequest;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.seller.dto.UpdatePasswordRequest;
import com.JVM.eCart.seller.dto.UpdateSellerProfileRequest;
import com.JVM.eCart.seller.entity.Seller;
import com.JVM.eCart.seller.repository.SellerRepository;
import com.JVM.eCart.user.entity.Address;
import com.JVM.eCart.user.entity.User;
import com.JVM.eCart.user.repository.AddressRepository;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private final AuthServiceImpl authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AddressRepository addressRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public String updateSellerProfile(UpdateSellerProfileRequest request) {

        User user = authService.getLoggedInUser();

        // update user fields
        if(request.email() != null)
            user.setEmail(request.email());

        if (request.firstName() != null)
            user.setFirstName(request.firstName());

        if (request.lastName() != null)
            user.setLastName(request.lastName());

        // Update seller fields (if exists)
        Seller seller = user.getSeller();

        if (seller != null) {
            if (request.companyName() != null)
                seller.setCompanyName(request.companyName());

            if (request.companyContact() != null)
                seller.setCompanyContact(request.companyContact());

            if (request.gst() != null)
                seller.setGst(request.gst());

            sellerRepository.save(seller);
        }
        userRepository.save(user);
        return "Profile updated successfully";
    }

    public String updatePassword(UpdatePasswordRequest request) {

        if(!request.password().equals(request.confirmPassword())) {
            throw new RuntimeException("Password and confirm password do not match");
        }

        User user = authService.getLoggedInUser();

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        userRepository.save(user);

        emailService.sendMail(
                user.getEmail(),
                "Password Updated",
                "Your password has been updated successfully. If you did not perform this action, please contact support immediately."
        );

        return "Password updated successfully";
    }

    public String updateAddress(Long addressId, AddressDto request) {

        User user = authService.getLoggedInUser();

        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));

        if(!address.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized access to address");

        if(request.addressLine() != null)
            address.setAddressLine(request.addressLine());

        if(request.city() != null)
            address.setCity(request.city());

        if(request.country() != null)
            address.setCountry(request.country());

        if(request.zipCode() != null)
            address.setZipCode(request.zipCode());

        if(request.state() != null)
            address.setState(request.state());

        addressRepository.save(address);

        return "Address updated successfully";
    }

    public String updateCustomerProfile(UpdateCustomerProfileRequest request) {
        User user = authService.getLoggedInUser();

        if(request.firstName() != null)
            user.setFirstName(request.firstName());
        if(request.lastName() != null)
            user.setLastName(request.lastName());
        if(request.email() != null)
            user.setEmail(request.email());

        userRepository.save(user);
        return "Profile updated successfully";
    }

    @Transactional
    public String addCustomerNewAddress(AddAddressRequest request) {

        User user = authService.getLoggedInUser();

        Address address = new Address();
        BeanUtils.copyProperties(request,address, "id");
        address.setUser(user);
        addressRepository.save(address);

        user.getAddresses().add(address);
        userRepository.save(user);
        return "New address has been added successfully";
    }

    public String deleteAddress(Long addressId) {

        User user = authService.getLoggedInUser();
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));

        if(!address.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized to delete this address");

        user.getAddresses().remove(address);
        addressRepository.delete(address);

        return "Address has been deleted successfully";
    }
}

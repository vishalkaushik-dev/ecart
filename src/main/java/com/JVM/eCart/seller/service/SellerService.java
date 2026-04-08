package com.JVM.eCart.seller.service;

import com.JVM.eCart.common.utils.UtilsHelper;
import com.JVM.eCart.security.jwt.UserPrincipal;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.seller.dto.SellerViewProfileResponse;
import com.JVM.eCart.seller.entity.Seller;
import com.JVM.eCart.seller.repository.SellerRepository;
import com.JVM.eCart.user.entity.Address;
import com.JVM.eCart.user.entity.User;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final UtilsHelper utilsHelper;

    public SellerViewProfileResponse viewProfile() {

        UserPrincipal userPrincipal = utilsHelper.getCurrentUserPrincipal();
        String email = userPrincipal.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Seller seller = user.getSeller();
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

        return new SellerViewProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                seller != null ? seller.getCompanyContact() : null,
                seller != null ? seller.getCompanyName() : null,
                seller != null ? seller.getGst() : null,
                addressDtoList
        );
    }

}

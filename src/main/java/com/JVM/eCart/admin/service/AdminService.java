package com.JVM.eCart.admin.service;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Page<RegisteredCustomerResponse> getAllRegistererdCustomers (int pageSize, int pageOffset, String sort, String email) {
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        return userRepository.findRegisteredCustomers(email, pageable);
    }

}

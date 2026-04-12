package com.JVM.eCart.customer.controller;

import com.JVM.eCart.category.service.CategoryService;
import com.JVM.eCart.customer.dto.AddAddressRequest;
import com.JVM.eCart.customer.dto.UpdateCustomerProfileRequest;
import com.JVM.eCart.customer.service.CustomerService;
import com.JVM.eCart.product.dto.CustomerAllProductsResponse;
import com.JVM.eCart.product.dto.CustomerProductResponse;
import com.JVM.eCart.product.service.ProductService;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.seller.dto.UpdatePasswordRequest;
import com.JVM.eCart.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/view-profile")
    public ResponseEntity<?> viewProfile() {
        return ResponseEntity.ok(customerService.viewProfile());
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses() {
        return ResponseEntity.ok(customerService.getAddresses());
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateCustomerProfileRequest request) {
        return ResponseEntity.ok(userService.updateCustomerProfile(request));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PatchMapping("/address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDto request) {
        return ResponseEntity.ok(userService.updateAddress(id, request));
    }

    @PostMapping("/address")
    public ResponseEntity<?> addCustomerNewAddress(@Valid @RequestBody AddAddressRequest request) {
        return ResponseEntity.ok(userService.addCustomerNewAddress(request));
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteAddress(id));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getAllCategory(@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesWithSellerView(categoryId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryByIdForCustomer(categoryId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<CustomerProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDetails(productId));
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(@RequestParam Long categoryId,
                                            @RequestParam(defaultValue = "10") int max,
                                            @RequestParam(defaultValue = "0") int offset,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(defaultValue = "asc") String order,
                                            @RequestParam(required = false) String query) {

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort != null ? sort : "id"));

        return ResponseEntity.ok(productService.getAllProductsCustomerView(categoryId, query, pageable));
    }

    @GetMapping("/products/{productId}/similar")
    public ResponseEntity<Page<CustomerAllProductsResponse>> getSimilarProducts(@PathVariable Long productId,
                                                                               @RequestParam(defaultValue = "10") int max,
                                                                               @RequestParam(defaultValue = "0") int offset,
                                                                               @RequestParam(required = false) String sort,
                                                                               @RequestParam(defaultValue = "asc") String order,
                                                                               @RequestParam(required = false) String query) {

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort != null ? sort : "id"));

        return ResponseEntity.ok(productService.getSimilarProducts(productId, query, pageable));
    }
}

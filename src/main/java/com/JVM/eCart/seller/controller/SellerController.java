package com.JVM.eCart.seller.controller;

import com.JVM.eCart.category.service.CategoryService;
import com.JVM.eCart.product.dto.AddProductRequest;
import com.JVM.eCart.product.service.ProductService;
import com.JVM.eCart.security.jwt.UserPrincipal;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.seller.dto.UpdatePasswordRequest;
import com.JVM.eCart.user.service.UserService;
import com.JVM.eCart.seller.dto.UpdateSellerProfileRequest;
import com.JVM.eCart.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@AllArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/view-profile")
    public ResponseEntity<?> viewProfile() {
        return ResponseEntity.ok(sellerService.viewProfile());
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateSellerProfileRequest request) {
        return ResponseEntity.ok(userService.updateSellerProfile(request));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PatchMapping("/address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDto request) {
        return ResponseEntity.ok(userService.updateAddress(id, request));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getAllCategory() {
        return ResponseEntity.ok(categoryService.getAllLeafCategories());
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(productService.addProduct(request, userPrincipal.getId()));
    }
}

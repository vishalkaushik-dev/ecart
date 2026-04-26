package com.JVM.eCart.seller.controller;

import com.JVM.eCart.category.service.CategoryService;
import com.JVM.eCart.order.dto.UpdateOrderStatusRequest;
import com.JVM.eCart.order.service.OrderService;
import com.JVM.eCart.product.dto.*;
import com.JVM.eCart.product.service.ProductService;
import com.JVM.eCart.product.service.ProductVariationService;
import com.JVM.eCart.security.jwt.UserPrincipal;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.seller.dto.UpdatePasswordRequest;
import com.JVM.eCart.user.service.UserService;
import com.JVM.eCart.seller.dto.UpdateSellerProfileRequest;
import com.JVM.eCart.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ProductVariationService productVariationService;
    private final OrderService orderService;

    @GetMapping("/view-profile")
    public ResponseEntity<?> viewProfile() {
        return ResponseEntity.ok(sellerService.viewProfile());
    }

    @PutMapping("/profile")
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

    @PostMapping("/product-variation")
    public ResponseEntity<?> addVariation(@Valid @RequestBody AddProductVariationRequest request) {
        return ResponseEntity.ok(productVariationService.addVariation(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(productService.getProduct(productId, userPrincipal.getId()));
    }

    @GetMapping("/product-variation/{variationId}")
    public ResponseEntity<?> getProductVariation(@PathVariable Long variationId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProductVariationResponse response = productVariationService.getProductVariation(variationId, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product")
    public Page<ProductResponseDto> getAllProducts(@RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(defaultValue = "0") int pageOffset,
                                                   @RequestParam(defaultValue = "id") String sort,
                                                   @RequestParam(defaultValue = "ASC", required = false) String order,
                                                   @RequestParam(required = false) String query,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Sort.Direction direction = (order != null && order.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(direction, sort));

        return productService.getAllProducts(query, pageable, userPrincipal.getId(), userPrincipal.getUsername());
    }

    @GetMapping("/product-variation")
    public Page<ProductVariationResponse> getAllProductVariation(@RequestParam(defaultValue = "10") int pageSize,
                                                                 @RequestParam(defaultValue = "0") int pageOffset,
                                                                 @RequestParam(defaultValue = "id") String sort,
                                                                 @RequestParam(defaultValue = "ASC", required = false) String order,
                                                                 @RequestParam(required = false) Long query,
                                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Sort.Direction direction = (order != null && order.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageOffset,pageSize, Sort.by(direction, sort));
        return productVariationService.getAllProductVariation(query, pageable, userPrincipal.getId());
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(productService.deleteProduct(productId, userPrincipal.getId()));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                           @RequestBody UpdateProductRequest request,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(productService.updateProduct(productId, request, userPrincipal.getId()));
    }

    @PutMapping("/product-variation/{variationId}")
    public ResponseEntity<?> updateProductVariation(@PathVariable Long variationId,
                                                    @RequestBody UpdateProductVariationRequest request,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(productVariationService.updateProductVariation(variationId, request, userPrincipal.getId()));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> viewAllOrders(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "dateCreated") String sort,
            @RequestParam(defaultValue = "desc") String order
    ) {
        Sort.Direction direction = order.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));
        return ResponseEntity.ok(orderService.viewSellerAllOrders(user.getId(), pageable));
    }

    @PatchMapping("/orders/update-status")
    public  ResponseEntity<?> updateOrderStatus(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UpdateOrderStatusRequest request){
        return ResponseEntity.ok(orderService.updateOrderStatus(userPrincipal.getId(), request));
    }
}

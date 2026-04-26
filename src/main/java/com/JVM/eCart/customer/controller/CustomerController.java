package com.JVM.eCart.customer.controller;

import com.JVM.eCart.category.service.CategoryService;
import com.JVM.eCart.customer.dto.AddAddressRequest;
import com.JVM.eCart.customer.dto.UpdateCustomerProfileRequest;
import com.JVM.eCart.customer.service.CustomerService;
import com.JVM.eCart.order.dto.*;
import com.JVM.eCart.order.service.CartService;
import com.JVM.eCart.order.service.OrderService;
import com.JVM.eCart.product.dto.CustomerAllProductsResponse;
import com.JVM.eCart.product.dto.CustomerProductResponse;
import com.JVM.eCart.product.service.ProductService;
import com.JVM.eCart.security.jwt.UserPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/view-profile")
    public ResponseEntity<?> viewProfile() {
        return ResponseEntity.ok(customerService.viewProfile());
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses() {
        return ResponseEntity.ok(customerService.getAddresses());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateCustomerProfileRequest request) {
        return ResponseEntity.ok(userService.updateCustomerProfile(request));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PatchMapping("/address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto request) {
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

    @PostMapping("/cart")
    public ResponseEntity<?> addToCart(@RequestBody @Valid AddToCartRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(cartService.addToCart(userPrincipal.getId(),request));
    }

    @GetMapping("/cart")
    public ResponseEntity<ViewCartResponse> viewCart(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(cartService.viewCart(userPrincipal.getId()));
    }

    @DeleteMapping("/cart/{productVariationId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long productVariationId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(cartService.deleteFromCart(userPrincipal.getId(), productVariationId));
    }

    @PutMapping("/cart")
    public ResponseEntity<String> updateCart(@Valid @RequestBody AddToCartRequest request, @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(cartService.updateCart(user.getId(), request));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<?> emptyCart(@AuthenticationPrincipal UserPrincipal user){
        return ResponseEntity.ok(cartService.emptyCart(user.getId()));
    }

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestParam(required = true) Long addressId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(orderService.placeOrder(addressId, userPrincipal.getId()));
    }

    @PostMapping("/orders/checkout")
    public ResponseEntity<?> placePartialOrder(@Valid @RequestBody PartialOrderRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(orderService.placePartialOrder(request, userPrincipal.getId()));
    }

    @PostMapping("/orders/buy-now")
    public ResponseEntity<?> placeDirectOrder(@Valid @RequestBody SingleOrderRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(orderService.placeDirectOrder(request, userPrincipal.getId()));
    }

    @PatchMapping("/order-products/{orderProductId}/cancel")
    public ResponseEntity<?> cancelOrderedProduct(@PathVariable Long orderProductId, @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(orderService.cancelOrderedProduct(userPrincipal.getId(), orderProductId));
    }

    @PatchMapping("/order-products/{orderProductId}/return")
    public ResponseEntity<?> returnOrderProduct(@PathVariable Long orderProductId, @RequestBody ReturnOrderRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(orderService.returnOrderProduct(userPrincipal.getId(), orderProductId, request));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ViewOrderResponse> viewOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(orderService.viewOrder(user.getId(), orderId));
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
        return ResponseEntity.ok(orderService.viewAllOrders(user.getId(), pageable, max, offset));
    }

}

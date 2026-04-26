package com.JVM.eCart.admin.controller;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.admin.dto.RegisteredSellerResponse;
import com.JVM.eCart.admin.service.AdminService;
import com.JVM.eCart.category.dto.*;
import com.JVM.eCart.category.service.CategoryService;
import com.JVM.eCart.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/registered-customers")
    public ResponseEntity<?> getRegisteredCustomers(@RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(defaultValue = "0") int pageOffset,
                                                    @RequestParam(defaultValue = "id") String sort,
                                                    @RequestParam(required = false) String email) {

        Page<RegisteredCustomerResponse> page = adminService.getAllRegisteredCustomers(pageSize, pageOffset, sort, email);

        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("customers", page.getContent());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/registered-sellers")
    public ResponseEntity<?> getRegisteredSellers(@RequestParam(defaultValue = "10") int pageSize,
                                                                         @RequestParam(defaultValue = "0") int pageOffset,
                                                                         @RequestParam(defaultValue = "id") String sort,
                                                                         @RequestParam(required = false) String email) {

        Page<RegisteredSellerResponse> page = adminService.getAllRegisteredSellers(pageSize,pageOffset,sort,email);
        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("sellers", page.getContent());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/customer/activate")
    public ResponseEntity<?> activateCustomer(@RequestParam String email) {
        return ResponseEntity.ok(adminService.activateCustomer(email));
    }

    @PatchMapping("/customer/deactivate")
    public ResponseEntity<?> deactivateCustomer(@RequestParam String email) {
        return ResponseEntity.ok(adminService.deactivateCustomer(email));
    }

    @PatchMapping("/seller/activate")
    public ResponseEntity<?> activateSeller(@RequestParam String email) {
        return ResponseEntity.ok(adminService.activateSeller(email));
    }

    @PatchMapping("/seller/deactivate")
    public ResponseEntity<?> deactivateSeller(@RequestParam String email) {
        return ResponseEntity.ok(adminService.deactivateSeller(email));
    }

    @PostMapping("/metadata-field")
    public ResponseEntity<?>  addMetaDataField(@Validated @RequestBody MetaDataFieldRequest request) {
        return ResponseEntity.ok(categoryService.addMetaDataField(request));
    }

    @GetMapping("/metadata-field")
    public ResponseEntity<?> getAllMetaDataFields(
            @RequestParam(defaultValue = "0") int offSet,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(categoryService.fetchMetaDataAllFields(offSet, max, sort, order, query));
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody CreateCategoryRequest request) {

        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<FetchCategoryResponse> get(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @GetMapping("/category")
    public ResponseEntity<List<FetchCategoryResponse>> getAllCategories(
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String query
    ) {
        CategoryFilterRequest request = new CategoryFilterRequest();
        if (max != null) request.setMax(max);
        if (offset != null) request.setOffset(offset);
        if (sort != null) request.setSort(sort);
        if (order != null) request.setOrder(order);
        if (query != null) request.setQuery(query);
        return ResponseEntity.ok(categoryService.getAllCategoriesWithAdminView(request));
    }

    @PutMapping("/category/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @PostMapping("/category/metadata")
    public ResponseEntity<String> addMetaDataWithCategory(@Valid @RequestBody CategoryMetadataFieldValuesRequest request) {
        return ResponseEntity.ok(categoryService.addMetadataFieldValuesWithCategory(request));
    }

    @PutMapping("/category/metadata")
    public ResponseEntity<String> updateMetadata(
            @Valid @RequestBody UpdateCategoryMetadataValuesRequest request) {

        return  ResponseEntity.ok(categoryService.updateMetadataValues(request));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductForAdmin(productId));
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false, defaultValue = "10") Integer max,
            @RequestParam(required = false,defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "ASC") String order,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId
    ) {
        Sort.Direction direction = (order != null && order.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));
        return ResponseEntity.ok(productService.getAllProductsForAdmin(categoryId, sellerId, pageable));
    }

    @PutMapping("/products/{productId}/deactivate")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deactivateProduct(productId));
    }

    @PutMapping("/products/{productId}/activate")
    public ResponseEntity<?> activateProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.activateProduct(productId));
    }
}

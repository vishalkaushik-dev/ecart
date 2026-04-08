package com.JVM.eCart.admin.controller;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.admin.dto.RegisteredSellerResponse;
import com.JVM.eCart.admin.service.AdminService;
import com.JVM.eCart.category.dto.*;
import com.JVM.eCart.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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

    @PatchMapping("/customers/{userId}/activate")
    public ResponseEntity<?> activateCustomer(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.activateCustomer(userId));
    }

    @PatchMapping("/customers/{userId}/deactivate")
    public ResponseEntity<?> deactivateCustomer(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deactivateCustomer(userId));
    }

    @PatchMapping("/sellers/{userId}/activate")
    public ResponseEntity<?> activateSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.activateSeller(userId));
    }

    @PatchMapping("/sellers/{userId}/deactivate")
    public ResponseEntity<?> deactivateSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deactivateSeller(userId));
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

}

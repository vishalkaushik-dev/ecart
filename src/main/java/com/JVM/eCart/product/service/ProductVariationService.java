package com.JVM.eCart.product.service;

import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.category.entity.CategoryMetadataFieldValue;
import com.JVM.eCart.category.repository.CategoryMetadataFieldValuesRepository;
import com.JVM.eCart.product.dto.AddProductVariationRequest;
import com.JVM.eCart.product.dto.ProductVariationResponse;
import com.JVM.eCart.product.dto.UpdateProductVariationRequest;
import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.entity.ProductVariation;
import com.JVM.eCart.product.repository.ProductRepository;
import com.JVM.eCart.product.repository.ProductVariationRepository;
import com.JVM.eCart.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductVariationService {

    private final ProductRepository productRepository;
    private final ProductVariationRepository variationRepository;
    private final ObjectMapper objectMapper;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    public String addVariation(AddProductVariationRequest request) {

        // 1. Validate Product
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new RuntimeException("Product is deleted");
        }

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new RuntimeException("Product is not active");
        }

        if (request.quantityAvailable() < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }

        if (request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Price cannot be negative");
        }

        if (request.metadata() == null || request.metadata().isEmpty()) {
            throw new RuntimeException("At least one metadata field is required");
        }

        // 4. Validate metadata structure consistency
        List<ProductVariation> existingVariations =
                variationRepository.findByProduct_Id(product.getId());

        if (!existingVariations.isEmpty()) {
            Map<String, String> existingMeta = existingVariations.get(0).getMetadata();

            System.out.println("Existing Metadata Keys: " + existingMeta.keySet());

            if (!existingMeta.keySet().equals(request.metadata().keySet())) {
                throw new RuntimeException("All variations must have same metadata structure");
            }
        }

        // Validate metadata values against category (IMPORTANT)
        validateMetadataWithCategory(product.getCategory(), request.metadata());

        // Validate Image format
        if (!isValidImage(request.primaryImageName())) {
            throw new RuntimeException("Invalid image format");
        }

        // Save Variation
        ProductVariation productVariation = new ProductVariation();
        productVariation.setProduct(product);
        productVariation.setQuantityAvailable(request.quantityAvailable());
        productVariation.setPrice(request.price());
        productVariation.setPrimaryImage(request.primaryImageName());
        productVariation.setIsActive(true);
        productVariation.setMetadata(request.metadata());

        variationRepository.save(productVariation);
        return "Product variation has been added successfully";
    }

    public ProductVariationResponse getProductVariation(Long variationId, Long userId) {

        // Validate ID
        ProductVariation variation = variationRepository.findById(variationId)
                .orElseThrow(() -> new RuntimeException("Invalid variation ID"));

        Product product = variation.getProduct();

        // Product should be non-deleted
        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new RuntimeException("Product is deleted");
        }

        if (!product.getSeller().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Not owner of product variation");
        }

        // Build response
        return new ProductVariationResponse(
                variation.getId(),
                variation.getPrice(),
                variation.getQuantityAvailable(),
                variation.getPrimaryImage(),
                variation.getSecondaryImages(),
                variation.getMetadata(),
                variation.getIsActive(),

                product.getId(),
                product.getName(),
                product.getDescription()
        );
    }

    public Page<ProductVariationResponse> getAllProductVariation(Long productVariationId, Pageable pageable, Long sellerUserId) {


        Page<ProductVariation> allProductVariations;
        if(productVariationId != null) {
            allProductVariations = variationRepository.findAllByIdAndSellerUserId(sellerUserId, productVariationId, pageable);
        } else {
            allProductVariations = variationRepository.findAllBySellerUserId(sellerUserId, pageable);
        }

        return allProductVariations.map(v -> new ProductVariationResponse(
                v.getId(),
                v.getPrice(),
                v.getQuantityAvailable(),
                v.getPrimaryImage(),
                v.getSecondaryImages(),
                v.getMetadata(),
                v.getIsActive(),
                v.getProduct().getId(),
                v.getProduct().getName(),
                v.getProduct().getDescription()
        ));
    }

    public String updateProductVariation(Long variationId, UpdateProductVariationRequest request, Long userId) {

        ProductVariation productVariation = variationRepository.findById(variationId).orElseThrow(() -> new RuntimeException("Product variation ID not found"));

        Product product = productVariation.getProduct();

        if(Boolean.TRUE.equals(product.getIsDeleted()))
            throw new RuntimeException("Product you want to update is deleted");

        if (!Boolean.TRUE.equals(product.getIsActive()))
            throw new RuntimeException("Product is not active");

        if(!product.getSeller().getUser().getId().equals(userId))
            throw new RuntimeException("Access denied: Not owner");

        if(request.quantityAvailable() != null){
            if(request.quantityAvailable() < 0)
                throw new RuntimeException("Price can not be negative");
            productVariation.setQuantityAvailable(request.quantityAvailable());
        }

        if (request.price() != null) {
            if (request.price().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Price cannot be negative");
            }
            productVariation.setPrice(request.price());
        }

        // Metadata validations and structure consistency
        if(request.metaData() != null && !request.metaData().isEmpty()) {
            List<ProductVariation> allVariations = variationRepository.findByProduct_Id(product.getId());

            if(!allVariations.isEmpty()) {
                Map<String,String> existingMeta = allVariations.get(0).getMetadata();

                if(!existingMeta.keySet().equals(request.metaData().keySet())) {
                    throw new RuntimeException("All variations must have same metadata structure");
                }
            }

            // Validate values against category
            validateMetadataWithCategory(product.getCategory(), request.metaData());

            productVariation.setMetadata(request.metaData());
        }

        if (request.primaryImage() != null) {
            if (!isValidImage(request.primaryImage())) {
                throw new RuntimeException("Invalid primary image format");
            }
            productVariation.setPrimaryImage(request.primaryImage());
        }

        if (request.secondaryImage() != null) {
            for (String img : request.secondaryImage()) {
                if (!isValidImage(img)) {
                    throw new RuntimeException("Invalid secondary image format");
                }
            }
            productVariation.setSecondaryImages(request.secondaryImage());
        }

        if(request.isActive() != null)
            productVariation.setIsActive(request.isActive());

        variationRepository.save(productVariation);
        return "Product variation updated successfully";
    }

    private boolean isValidImage(String imageName) {
        return imageName.endsWith(".jpg") ||
                imageName.endsWith(".jpeg") ||
                imageName.endsWith(".png");
    }

    private void validateMetadataWithCategory(Category category,
                                              Map<String, String> metadata) {

        // Fetch all metadata values for category
        List<CategoryMetadataFieldValue> fieldValues =
                categoryMetadataFieldValuesRepository.findByCategory_Id(category.getId());

        // Build map: field -> allowed values (Set)
        Map<String, Set<String>> validMap = new HashMap<>();

        for (CategoryMetadataFieldValue fv : fieldValues) {

            String fieldName = fv.getMetadataField().getName();

            // Split CSV → "S,M,L" → ["S","M","L"]
            Set<String> values = Arrays.stream(fv.getValues().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

            validMap.put(fieldName, values);
        }

        // Validate incoming metadata
        for (Map.Entry<String, String> entry : metadata.entrySet()) {

            String field = entry.getKey();
            String value = entry.getValue();

            if (!validMap.containsKey(field)) {
                throw new RuntimeException("Invalid metadata field: " + field);
            }

            if (!validMap.get(field).contains(value)) {
                throw new RuntimeException(
                        "Invalid value '" + value + "' for field '" + field + "'"
                );
            }
        }
    }
}

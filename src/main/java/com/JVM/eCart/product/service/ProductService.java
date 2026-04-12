package com.JVM.eCart.product.service;

import com.JVM.eCart.auth.service.EmailService;
import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.category.repository.CategoryRepository;
import com.JVM.eCart.product.dto.*;
import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.entity.ProductVariation;
import com.JVM.eCart.product.repository.ProductRepository;
import com.JVM.eCart.product.repository.ProductVariationRepository;
import com.JVM.eCart.security.jwt.UserPrincipal;
import com.JVM.eCart.seller.entity.Seller;
import com.JVM.eCart.seller.repository.SellerRepository;
import com.JVM.eCart.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final EmailService emailService;
    private final ProductVariationRepository variationRepository;

    @Transactional
    public String addProduct(AddProductRequest request, Long userId) {

        Seller seller = sellerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Seller not found"));
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new RuntimeException("Category not found"));

        if(categoryRepository.existsByParentCategory_Id(request.categoryId())) {
            throw new RuntimeException("Category must be a leaf node, Cannot add product to non-leaf category");
        }

        boolean isProductExists = productRepository.existsByNameAndBrandAndCategory_IdAndSellerUserId(
                request.name(),
                request.brand(),
                request.categoryId(),
                userId
        );

        if (isProductExists) {
            throw new RuntimeException("Product already exists for Seller: "+ seller.getUser().getFirstName() + "Brand : " + request.brand() + "category : " + category.getName());
        }

        Product product = new Product();
        product.setName(request.name());
        product.setBrand(request.brand());
        product.setCategory(category);
        product.setDescription(request.description());
        product.setIsCancellable(request.isCancellable());
        product.setIsReturnable(request.isReturnable());
        product.setIsActive(false);
        product.setSeller(seller);

        productRepository.save(product);

        // send email to Admin
        emailService.sendMail(
                "vishal.kaushik@tothenew.com",
                "Product Added: " + product.getName(),
                "New product '" + product.getName() + "' has been added by Seller: "+ seller.getUser().getEmail() +  " and is pending approval."
        );
        return "Product added successfully";
    }

    public ProductResponseDto getProduct(Long id, Long userId) {

        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        Seller seller = sellerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Seller not found"));

        System.out.println("Product Seller: " + product.getSeller().getUser().getEmail());
        System.out.println("Requesting Seller: " + seller.getUser().getEmail());

        if(!product.getSeller().getUser().getEmail().equals(seller.getUser().getEmail())) {
            throw new RuntimeException("Access denied: Not product owner");
        }

        System.out.println("Product Category: " + product.getCategory().getName());
        CategoryDto categoryDto = mapCategory(product.getCategory());

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getIsActive(),
                product.getBrand(),
                product.getIsDeleted(),
                categoryDto,
                product.getSeller().getUser().getEmail()
        );
    }

    public Page<ProductResponseDto> getAllProducts(String productName, Pageable pageable, Long userId, String sellerEmail) {

        Page<Product> products;
        if(productName != null && !productName.isBlank()) {
            products = productRepository.findByNameIgnoreCaseAndSeller_User_IdAndIsDeletedFalse(productName, userId, pageable);
        } else {
            products = productRepository.findBySeller_User_IdAndIsDeletedFalse(userId, pageable);
        }

        return products.map(product -> new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getIsActive(),
                product.getBrand(),
                product.getIsDeleted(),
                mapCategory(product.getCategory()),
                sellerEmail
        ));
    }

    public String deleteProduct(Long productId, Long userId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Invalid Product Id"));

        if(Boolean.TRUE.equals(product.getIsDeleted()))
            throw new RuntimeException("Product already deleted");

        if(!product.getSeller().getUser().getId().equals(userId))
            throw new RuntimeException("Access denied: You are not the owner of this product");

        product.setIsDeleted(true);
        product.setIsActive(false);
        productRepository.save(product);

        return "Product deleted successfully";
    }

    public String updateProduct(Long productId, UpdateProductRequest request, Long userId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Invalid Product Id"));

        if(!product.getSeller().getUser().getId().equals(userId))
            throw new RuntimeException("Access denied: You are not the owner of this product");

        if(Boolean.TRUE.equals(product.getIsDeleted()))
            throw new RuntimeException("Product is deleted, you can't update it.");

        if(request.name() != null && !request.name().equals(product.getName())) {

            boolean exists = productRepository.existsByNameAndBrandAndCategory_IdAndSeller_User_IdAndIsDeletedFalse(
                    request.name(),
                    product.getBrand(),
                    product.getCategory().getId(),
                    userId
            );

            if(exists)
                throw new RuntimeException("Product name must be unique for given brand, category and seller");

            product.setName(request.name());
        }

        if(request.description() != null)
            product.setDescription(request.description());

        if(request.isCancellable() != null)
            product.setIsCancellable(request.isCancellable());

        if(request.isReturnable() != null)
            product.setIsReturnable(request.isReturnable());

        productRepository.save(product);
        return "Product updated successfully";
    }

    public CustomerProductResponse getProductDetails(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(Boolean.TRUE.equals(product.getIsDeleted()))
            throw new RuntimeException("Product you are trying to fetch is deleted");
        if(!Boolean.TRUE.equals(product.getIsActive()))
            throw new RuntimeException("Product you are trying to fetch is not active");

        // fetch product variations
        List<ProductVariation> variations = variationRepository.findByProduct_IdAndIsActiveTrue(productId);
        if(variations.isEmpty())
            throw new RuntimeException("Product has no active validations");

        List<CustomerProductVariationResponse> variationDtos = variations.stream()
                .map((v) -> new CustomerProductVariationResponse(
                        v.getId(),
                        v.getPrice(),
                        v.getQuantityAvailable(),
                        v.getPrimaryImage(),
                        v.getSecondaryImages(),
                        v.getMetadata(),
                        v.getIsActive()
                )).toList();

        return new CustomerProductResponse(
          product.getId(),
          product.getName(),
          product.getDescription(),
          product.getBrand(),
          product.getCategory().getId(),
                product.getCategory().getName(),
                variationDtos
        );
    }

    public Page<CustomerAllProductsResponse> getAllProductsCustomerView(Long categoryId, String query, Pageable pageable) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Invalid category"));

        if(categoryRepository.existsByParentCategory_Id(categoryId))
            throw new RuntimeException("Category must be a leaf node");

        Page<Product> products = productRepository.findAllProducts(categoryId, query,pageable);

        return products.map(this::mapToAllProductCustomerResponse);
    }

    public Page<CustomerAllProductsResponse> getSimilarProducts(Long productId, String query, Pageable pageable) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product Id not found"));

        if(!(product.getIsActive()) || Boolean.TRUE.equals(product.getIsDeleted()))
            throw new RuntimeException("Invalid Product");

        Page<Product> products = productRepository.findSimilarProducts(
                product.getCategory().getId(),
                productId,
                product.getBrand(),
                query,
                pageable
        );

        return products.map(this::mapToAllProductCustomerResponse);
    }

    @Transactional
    public AdminProductResponse getProductForAdmin(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductVariation> variations = product.getVariations();

        List<AdminVariationResponse> variationResponse = variations.stream()
                .map(v -> new AdminVariationResponse(
                        v.getId(),
                        v.getQuantityAvailable(),
                        v.getPrice(),
                        v.getPrimaryImage(),
                        v.getSecondaryImages(),
                        v.getMetadata(), // already Map (Hibernate 6/7)
                        v.getIsActive()
                )).toList();

        return new AdminProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getIsCancellable(),
                product.getIsReturnable(),
                product.getIsActive(),
                product.getIsDeleted(),
                new AdminCategoryResponse(
                        product.getCategory().getId(),
                        product.getCategory().getName()
                ),
                variationResponse
        );
    }

    public Page<AdminAllProductResponse> getAllProductsForAdmin(Long categoryId, Long sellerId, Pageable pageable) {

        Page<Product> productPage = productRepository.findAllProductsWithQuery(categoryId, sellerId, pageable);
        List<Product> products = productPage.getContent();

        List<ProductVariation> variations = variationRepository.findByProductIn(products);

        List<String> primaryImageList = variations.stream().map(ProductVariation::getPrimaryImage).toList();

        return productPage.map((p) -> new AdminAllProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getBrand(),
                p.getIsCancellable(),
                p.getIsReturnable(),
                p.getIsActive(),
                p.getIsDeleted(),
                new AdminCategoryResponse(
                        p.getCategory().getId(),
                        p.getCategory().getName()
                ),
                primaryImageList
        ));
    }

    @Transactional
    public String deactivateProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(Boolean.FALSE.equals(product.getIsActive()))
            throw new RuntimeException("Product is already inactive");

        product.setIsActive(false);
        productRepository.save(product);

        sendProductStatusEmail(product, "deactivated");
        return "Product deactivated successfully";
    }

    @Transactional
    public String activateProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(Boolean.TRUE.equals(product.getIsActive()))
            throw new RuntimeException("Product is already active");

        product.setIsActive(true);
        productRepository.save(product);

        sendProductStatusEmail(product, "activated");
        return "Product activated successfully";
    }

    private void sendProductStatusEmail(Product product, String status) {

        User seller = product.getSeller().getUser();

        String subject = "Product " + status;
        String body = "Hello " + seller.getFirstName() + ",\n\n"
                + "Your product '" + product.getName() + "' has been " + status + " by admin.\n"
                + "Brand: " + product.getBrand() + "\n"
                + "Category: " + product.getCategory().getName() + "\n\n"
                + "Regards,\nAdmin Team";

        emailService.sendMail(seller.getEmail(), subject, body);
    }

    private CustomerAllProductsResponse mapToAllProductCustomerResponse(Product product) {

        List<String> primaryImages = product.getVariations().stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .map(ProductVariation::getPrimaryImage)
                .filter(Objects::nonNull)
                .toList();

        return new CustomerAllProductsResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                new CustomerAllProductsCategoryResponse(product.getCategory().getId(), product.getCategory().getName()),
                primaryImages
        );
    }

    private CategoryDto mapCategory(Category category) {

        if (category == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());

        if (category.getParentCategory() != null) {
            dto.setParent(mapCategory(category.getParentCategory()));
        }

        return dto;
    }

}

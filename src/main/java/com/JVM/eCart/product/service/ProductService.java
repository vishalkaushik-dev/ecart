package com.JVM.eCart.product.service;

import com.JVM.eCart.auth.service.EmailService;
import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.category.repository.CategoryRepository;
import com.JVM.eCart.product.dto.AddProductRequest;
import com.JVM.eCart.product.dto.CategoryDto;
import com.JVM.eCart.product.dto.ProductResponseDto;
import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.repository.ProductRepository;
import com.JVM.eCart.security.jwt.UserPrincipal;
import com.JVM.eCart.seller.entity.Seller;
import com.JVM.eCart.seller.repository.SellerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final EmailService emailService;

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

    public Page<ProductResponseDto> getAllProducts(Pageable pageable, Long userId, String sellerEmail) {

        Page<Product> products = productRepository.findBySeller_User_IdAndIsDeletedFalse(userId, pageable);

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

package com.JVM.eCart.product.service;

import com.JVM.eCart.auth.service.EmailService;
import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.category.repository.CategoryRepository;
import com.JVM.eCart.product.dto.AddProductRequest;
import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.repository.ProductRepository;
import com.JVM.eCart.seller.entity.Seller;
import com.JVM.eCart.seller.repository.SellerRepository;
import lombok.AllArgsConstructor;
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

}

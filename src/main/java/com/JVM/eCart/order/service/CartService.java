package com.JVM.eCart.order.service;

import com.JVM.eCart.auth.repository.CustomerRepository;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.order.dto.AddToCartRequest;
import com.JVM.eCart.order.dto.CartItemResponse;
import com.JVM.eCart.order.dto.ViewCartResponse;
import com.JVM.eCart.order.entity.Cart;
import com.JVM.eCart.order.repository.CartRepository;
import com.JVM.eCart.product.entity.ProductVariation;
import com.JVM.eCart.product.repository.ProductVariationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {

    private final CustomerRepository customerRepository;
    private final ProductVariationRepository variationRepository;
    private final CartRepository cartRepository;

    public String addToCart(Long userId, AddToCartRequest request) {

        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));

        ProductVariation productVariation = variationRepository.findById(request.productVariationId()).orElseThrow(() -> new RuntimeException("Product variation not found"));

        if(!productVariation.getIsActive())
            throw new RuntimeException("Product variation is inactive");

        if(productVariation.getProduct().getIsDeleted())
            throw new RuntimeException("Product is deleted");

        if(request.quantity() > productVariation.getQuantityAvailable())
            throw new RuntimeException("Insufficient stock");

        Optional<Cart> existingCart = cartRepository.findByCustomerAndProductVariation(customer, productVariation);

        if(existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + request.quantity());
            cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cart.setProductVariation(productVariation);
            cart.setQuantity(request.quantity());
            cart.setIsWishItem(false);
            cartRepository.save(cart);
        }
        return "Product added to cart successfully";
    }

    public ViewCartResponse viewCart(Long userId) {

        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Cart> cartList = cartRepository.findByCustomer(customer);

        BigDecimal total = cartList.stream()
                .map(c -> c.getProductVariation().getPrice()
                        .multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CartItemResponse> carItems = cartList.stream().map((cart) -> {

            ProductVariation pv = cart.getProductVariation();

            boolean isOutOfStock = !pv.getIsActive() || pv.getQuantityAvailable() == 0;

            return new CartItemResponse(
                    pv.getId(),
                    pv.getProduct().getName(),
                    pv.getPrice(),
                    cart.getQuantity(),
                    pv.getPrimaryImage(),
                    pv.getMetadata(),
                    isOutOfStock
            );
        }).toList();

        return new ViewCartResponse(
                carItems,
                total
        );
    }

    public String deleteFromCart(Long userId, Long productVariationId){

        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));

        ProductVariation productVariation = variationRepository.findById(productVariationId).orElseThrow(() -> new RuntimeException("Product variation not found"));

        Cart cart = cartRepository.findByCustomerAndProductVariation(customer,productVariation).orElseThrow(() -> new RuntimeException("Product not present in cart"));

        cartRepository.delete(cart);
        return "Product removed from cart successfully";
    }

    public String updateCart(Long userId, AddToCartRequest request){

        ProductVariation pv = variationRepository.findById(request.productVariationId()).orElseThrow(() -> new RuntimeException("Product variation not found"));

        if(!pv.getIsActive())
            throw new RuntimeException("Product variation is inactive");

        if(pv.getProduct().getIsDeleted())
            throw new RuntimeException("Product is deleted");

        Cart cart = cartRepository
                .findByCustomer_User_IdAndProductVariation_Id(userId, pv.getId())
                .orElseThrow(() -> new RuntimeException("Product not present in cart"));

        if(request.quantity() == 0)
            cartRepository.delete(cart);
        else {
            if (request.quantity() > pv.getQuantityAvailable()) {
                throw new RuntimeException("Insufficient stock");
            }

            cart.setQuantity(request.quantity());
            cartRepository.save(cart);
        }
        return "Product is updated in the cart.";
    }

    public String emptyCart(Long userId){

        int deletedCount = cartRepository.deleteByCustomer_User_Id(userId);

        if(deletedCount == 0)
            throw new RuntimeException("Cart is already empty");

        return "Cart emptied successfully";

    }
}

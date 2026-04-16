package com.JVM.eCart.order.service;

import com.JVM.eCart.auth.repository.CustomerRepository;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.order.entity.Cart;
import com.JVM.eCart.order.entity.Order;
import com.JVM.eCart.order.entity.OrderProduct;
import com.JVM.eCart.order.repository.CartRepository;
import com.JVM.eCart.order.repository.OrderProductRepository;
import com.JVM.eCart.order.repository.OrderRepository;
import com.JVM.eCart.product.entity.ProductVariation;
import com.JVM.eCart.user.entity.Address;
import com.JVM.eCart.user.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OrderService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    public String placeOrder(Long addressId, Long userId) {

        //fetch customer details
        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found with address Id: "+ addressId));

        if (!Objects.equals(customer.getUser().getId(), address.getUser().getId()))
            throw new RuntimeException("Address does not belong to the specified customer");


        List<Cart> cartItems = cartRepository.findByCustomer(customer);
        if(cartItems.isEmpty())
            throw new RuntimeException("Cart is empty");

        BigDecimal totalAmount = BigDecimal.ZERO;

        // validate all items
        for(Cart cart : cartItems) {

            ProductVariation pv = cart.getProductVariation();

            if (!pv.getIsActive())
                throw new RuntimeException("Product variation inactive");

            if (pv.getProduct().getIsDeleted())
                throw new RuntimeException("Product deleted");

            if (cart.getQuantity() > pv.getQuantityAvailable())
                throw new RuntimeException("Insufficient stock for productVariationId: " + pv.getId());

            totalAmount = totalAmount.add(
                    pv.getPrice().multiply(BigDecimal.valueOf(pv.getQuantityAvailable()))
            );
        }

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setAmountPaid(totalAmount);
        order.setDateCreated(LocalDateTime.now());
        order.setPaymentMethod("COD");

        // set Address
        order.setCity(address.getCity());
        order.setState(address.getState());
        order.setCountry(address.getCountry());
        order.setAddressLine(address.getAddressLine());
        order.setZipCode(address.getZipCode());
        order.setLabel(address.getLabel());

        orderRepository.save(order);

        // create orderProduct entries
        for(Cart cart : cartItems) {

            ProductVariation pv = cart.getProductVariation();

            OrderProduct op = new OrderProduct();
            op.setOrder(order);
            op.setProductVariation(pv);
            op.setQuantity(cart.getQuantity());
            op.setPrice(pv.getPrice());

            orderProductRepository.save(op);

            pv.setQuantityAvailable(pv.getQuantityAvailable()- cart.getQuantity());
        }
        cartRepository.deleteByCustomer_User_Id(userId);

        return "Order placed successfully. Order ID: " + order.getId();
    }

}

package com.JVM.eCart.order.service;

import com.JVM.eCart.auth.repository.CustomerRepository;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.order.dto.*;
import com.JVM.eCart.order.entity.Cart;
import com.JVM.eCart.order.entity.Order;
import com.JVM.eCart.order.entity.OrderProduct;
import com.JVM.eCart.order.entity.OrderStatus;
import com.JVM.eCart.order.enums.OrderStatusEnum;
import com.JVM.eCart.order.repository.CartRepository;
import com.JVM.eCart.order.repository.OrderProductRepository;
import com.JVM.eCart.order.repository.OrderRepository;
import com.JVM.eCart.order.repository.OrderStatusRepository;
import com.JVM.eCart.product.entity.ProductVariation;
import com.JVM.eCart.product.repository.ProductVariationRepository;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.user.entity.Address;
import com.JVM.eCart.user.repository.AddressRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductVariationRepository variationRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Transactional(rollbackFor = Exception.class)
    public String placeOrder(Long addressId, Long userId) {

        //fetch customer details
        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found with address Id: " + addressId));

        log.info("Place order with address {}",addressId);

        if (!Objects.equals(customer.getUser().getId(), address.getUser().getId()))
            throw new RuntimeException("Address does not belong to the specified customer");


        List<Cart> cartItems = cartRepository.findByCustomer(customer);
        if (cartItems.isEmpty())
            throw new RuntimeException("Cart is empty");

        BigDecimal totalAmount = validateAndCalculateTotal(cartItems);
        Order order = createOrder(customer, address, totalAmount, "COD");
        orderRepository.save(order);

        // create orderProduct entries
        List<OrderProduct> orderProducts = createOrderProductList(cartItems, order, OrderStatusEnum.ORDER_PLACED);

        orderProductRepository.saveAll(orderProducts);
        cartRepository.deleteByCustomer_User_Id(userId);

        List<OrderStatus> statusList = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            statusList.add(createInitialStatus(orderProduct));
        }
        orderStatusRepository.saveAll(statusList);

        return "Order placed successfully. Order ID: " + order.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> placePartialOrder(PartialOrderRequest request, Long userId) {

        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));
        Address address = addressRepository.findById(request.addressId()).orElseThrow(() -> new RuntimeException("Address not found with address Id: "+ request.addressId()));

        if (!Objects.equals(customer.getUser().getId(), address.getUser().getId()))
            throw new RuntimeException("Address does not belong to the specified customer");

        // Fetch only selected cart items
        List<Cart> selectedCartItems = cartRepository.findByCustomer_User_IdAndProductVariation_IdIn(userId, request.productVariationIds());

        if(selectedCartItems.isEmpty())
            throw new RuntimeException("No valid products found in cart");

        if(selectedCartItems.size() != request.productVariationIds().size())
            throw new RuntimeException("Some products are not present in cart");

        BigDecimal totalAmount = validateAndCalculateTotal(selectedCartItems);
        Order order = createOrder(customer, address, totalAmount, "Paytm");
        orderRepository.save(order);

        List<OrderProduct> orderProductList = createOrderProductList(selectedCartItems, order, OrderStatusEnum.ORDER_PLACED);
        orderProductRepository.saveAll(orderProductList);
        cartRepository.deleteAll(selectedCartItems);

        List<OrderStatus> statusList = new ArrayList<>();
        for (OrderProduct orderProduct : orderProductList) {
            statusList.add(createInitialStatus(orderProduct));
        }
        orderStatusRepository.saveAll(statusList);

        return Map.of(
                "message", "Partial order placed successfully",
                "orderId", order.getId()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> placeDirectOrder(SingleOrderRequest request, Long userId) {

        Customer customer = customerRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Customer not found"));

        ProductVariation pv = variationRepository.findById(request.productVariationId()).orElseThrow(() -> new RuntimeException("Product variation not found"));

        Address address = addressRepository.findById(request.addressId()).orElseThrow(() -> new RuntimeException("Address not found with address Id: "+ request.addressId()));

        if (!pv.getIsActive()) {
            throw new RuntimeException("Product variation is inactive");
        }

        if (pv.getProduct().getIsDeleted()) {
            throw new RuntimeException("Product is deleted");
        }

        if (request.quantity() > pv.getQuantityAvailable()) {
            throw new RuntimeException("Insufficient stock");
        }

        BigDecimal totalAmount = pv.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        Order order = createOrder(customer, address, totalAmount, "COD");
        orderRepository.save(order);

        OrderProduct op = new OrderProduct();
        op.setOrder(order);
        op.setProductVariation(pv);
        op.setQuantity(request.quantity());
        op.setPrice(pv.getPrice());
        orderProductRepository.save(op);

        pv.setQuantityAvailable(pv.getQuantityAvailable() - request.quantity());

        orderStatusRepository.save(createInitialStatus(op)); // setting initial status in Order status repo

        return Map.of(
                "message", "Order placed successfully",
                "orderId", order.getId()
        );
    }

    public String cancelOrderedProduct(Long userId, Long orderProductId) {

        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new RuntimeException("Order product not found"));

        // Valid ownership of product with its customer
        Long orderOwnerId = orderProduct.getOrder().getCustomer().getUser().getId();

        if(!orderOwnerId.equals(userId))
            throw new RuntimeException("Unauthorized access to cancel the order");

        OrderStatus orderStatus = orderStatusRepository.findTopByOrderProductOrderByTransitionDateDesc(orderProduct)
                .orElseThrow(() -> new RuntimeException("Order status not found"));

        OrderStatusEnum currentStatus = orderStatus.getToStatus(); // get current status

        // validate transition
        if(!currentStatus.canTransitionTo(OrderStatusEnum.CANCELLED))
            throw new RuntimeException("Cannot cancel order in current status: " + currentStatus);

        OrderStatus cancelledStatus = new OrderStatus();
        cancelledStatus.setOrderProduct(orderProduct);
        cancelledStatus.setFromStatus(currentStatus);
        cancelledStatus.setToStatus(OrderStatusEnum.CANCELLED);
        cancelledStatus.setTransitionNotesComments("Cancelled by user");
        cancelledStatus.setTransitionDate(LocalDateTime.now());
        orderStatusRepository.save(cancelledStatus);

        return "Ordered has been cancelled successfully";
    }

    public String returnOrderProduct(Long userId, Long orderProductId, ReturnOrderRequest request) {

        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new RuntimeException("Order product not found"));

        Long orderOwnerId = orderProduct.getOrder().getCustomer().getUser().getId();
        if (!orderOwnerId.equals(userId)) {
            throw new RuntimeException("Unauthorized access to cancel the order");
        }

//        OrderStatus orderCurrentStatus = orderStatusRepository.findTopByOrderProductOrderByTransitionDateDesc(orderProduct)
//                .orElseThrow(() -> new RuntimeException("Order status not found"));

        OrderStatusEnum currentStatus = orderProduct.getCurrentStatus();

        if(currentStatus != OrderStatusEnum.DELIVERED)
            throw new RuntimeException("Only delivered products can be returned");

        if(currentStatus.canTransitionTo(OrderStatusEnum.RETURN_REQUESTED))
            throw new RuntimeException("Return not allowed for status: " + currentStatus);

        OrderStatus returnOrderStatus = new OrderStatus();
        returnOrderStatus.setOrderProduct(orderProduct);
        returnOrderStatus.setFromStatus(currentStatus);
        returnOrderStatus.setToStatus(OrderStatusEnum.RETURN_REQUESTED);
        returnOrderStatus.setTransitionNotesComments(request.transitionNotesComments());
        returnOrderStatus.setTransitionDate(LocalDateTime.now());
        orderStatusRepository.save(returnOrderStatus);

        return "Return request initiated successfully";
    }

    @Transactional(readOnly = true)
    public ViewOrderResponse viewOrder(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if(!order.getCustomer().getUser().getId().equals(userId))
            throw new RuntimeException("Unauthorized access to view the order");

        List<OrderProduct> orderProducts = orderProductRepository.findByOrder(order);

        List<OrderItemResponse> items = orderProducts.stream()
                .map(op -> {
//                    OrderStatus currentStatus = orderStatusRepository.findTopByOrderProductOrderByTransitionDateDesc(op)
//                            .orElseThrow(() -> new RuntimeException("Order Status not found"));

                    ProductVariation opProductVariation = op.getProductVariation();

                    return new OrderItemResponse(
                            op.getId(),
                            opProductVariation.getId(),
                            opProductVariation.getProduct().getName(),
                            opProductVariation.getPrimaryImage(),
                            op.getQuantity(),
                            op.getPrice(),
                            op.getCurrentStatus()
//                            currentStatus.getToStatus()
                    );
                }).toList();

        OrderAddressResponse addressResponse = new OrderAddressResponse(
                order.getAddressLine(),
                order.getCity(),
                order.getState(),
                order.getCountry(),
                order.getZipCode(),
                order.getLabel()
        );

        return new ViewOrderResponse(
                order.getId(),
                order.getAmountPaid(),
                order.getPaymentMethod(),
                order.getDateCreated(),
                addressResponse,
                items
        );
    }

    @Transactional(readOnly = true)
    public OrderListResponse viewAllOrders(Long userId, Pageable pageable, int max, int offset) {

        Page<Order> orderPage = orderRepository.findByCustomer_User_Id(userId, pageable);

        List<OrderSummary> orders = orderPage.getContent().stream()
                .map(order -> {
//                    OrderStatusEnum latestStatus = getOverallStatus(order);

                    return new OrderSummary(
                            order.getId(),
                            order.getAmountPaid(),
                            order.getPaymentMethod(),
                            order.getDateCreated()
                    );
                }).toList();

        return new OrderListResponse(orders, offset, max, orderPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<SellerOrderResponse> viewSellerAllOrders(Long userId, Pageable pageable) {

        Page<Order> orders = orderRepository.findOrdersBySellerUserId(userId, pageable);
        return orders.map(order -> mapToResponse(order, userId));
    }

    @Transactional()
    public String updateOrderStatus(Long userId, UpdateOrderStatusRequest request){

        OrderProduct op = orderProductRepository.findById(request.orderProductId())
                .orElseThrow(() -> new RuntimeException("Order product not found"));

        Long sellerId = op.getProductVariation().getProduct().getSeller().getUser().getId();
        if (!sellerId.equals(userId)) {
            throw new RuntimeException("Unauthorized to update status");
        }

        if (!op.getCurrentStatus().equals(request.fromStatus())) {
            throw new RuntimeException("Invalid current status");
        }

        if (!request.fromStatus().canTransitionTo(request.toStatus())) {
            throw new RuntimeException("Transition not allowed");
        }

        op.setCurrentStatus(request.toStatus());
        orderProductRepository.save(op);

        OrderStatus history = new OrderStatus();
        history.setOrderProduct(op);
        history.setFromStatus(request.fromStatus());
        history.setToStatus(request.toStatus());
        history.setTransitionNotesComments(request.notes());
        history.setTransitionDate(LocalDateTime.now());
        orderStatusRepository.save(history);

        return "Order status updated successfully";
    }

    private SellerOrderResponse mapToResponse(Order order, Long userId) {

        List<SellerOrderItem> items = orderProductRepository
                .findByOrder(order)
                .stream()
                .filter(op -> op.getProductVariation()
                        .getProduct()
                        .getSeller()
                        .getUser()
                        .getId()
                        .equals(userId))
                .map(op -> new SellerOrderItem(
                        op.getId(),
                        op.getQuantity(),
                        op.getPrice(),
                        op.getProductVariation().getProduct().getName(),
                        op.getProductVariation().getProduct().getBrand(),
                        op.getProductVariation().getPrimaryImage(),
                        op.getCurrentStatus()
                ))
                .toList();

        return new SellerOrderResponse(
                order.getId(),
                order.getAmountPaid(),
                order.getDateCreated(),
                order.getPaymentMethod(),
                items
        );
    }

    private BigDecimal validateAndCalculateTotal(List<Cart> carts) {
        BigDecimal total = BigDecimal.ZERO;

        for (Cart cart : carts) {
            ProductVariation pv = cart.getProductVariation();

            if (!pv.getIsActive())
                throw new RuntimeException("Product variation inactive");

            if (pv.getProduct().getIsDeleted())
                throw new RuntimeException("Product deleted");

            if (cart.getQuantity() > pv.getQuantityAvailable())
                throw new RuntimeException("Insufficient stock for productVariationId: " + pv.getId());

            total = total.add(
                    pv.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))
            );
        }
        return total;
    }

    private Order createOrder(Customer customer, Address address, BigDecimal amount, String paymentMethod) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setAmountPaid(amount);
        order.setDateCreated(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod);

        order.setCity(address.getCity());
        order.setState(address.getState());
        order.setCountry(address.getCountry());
        order.setAddressLine(address.getAddressLine());
        order.setZipCode(address.getZipCode());
        order.setLabel(address.getLabel());

        return orderRepository.save(order);
    }

    private List<OrderProduct> createOrderProductList(List<Cart> cartItems, Order order, OrderStatusEnum orderStatus) {

        List<OrderProduct> orderProducts = new ArrayList<>();

        for (Cart cart : cartItems) {

            ProductVariation pv = cart.getProductVariation();

            OrderProduct op = new OrderProduct();
            op.setOrder(order);
            op.setProductVariation(pv);
            op.setQuantity(cart.getQuantity());
            op.setPrice(pv.getPrice());
            op.setCurrentStatus(orderStatus);

            orderProducts.add(op);
            pv.setQuantityAvailable(pv.getQuantityAvailable() - cart.getQuantity());
        }
        return orderProducts;
    }

    private OrderStatus createInitialStatus(OrderProduct orderProduct) {

        OrderStatus status = new OrderStatus();

        status.setOrderProduct(orderProduct);
        status.setFromStatus(null);
        status.setToStatus(OrderStatusEnum.ORDER_PLACED);
        status.setTransitionNotesComments("Order placed");
        status.setTransitionDate(LocalDateTime.now());

       return status;
    }
}

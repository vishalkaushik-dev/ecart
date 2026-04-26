package com.JVM.eCart.order.entity;

import com.JVM.eCart.order.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status")
@Data
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum fromStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum toStatus;
    private String transitionNotesComments;
    private LocalDateTime transitionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProduct orderProduct;

}

package com.JVM.eCart.order.enums;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {

    ORDER_PLACED,
    CANCELLED,
    ORDER_REJECTED,
    ORDER_CONFIRMED,
    ORDER_SHIPPED,
    DELIVERED,
    RETURN_REQUESTED,
    RETURN_REJECTED,
    RETURN_APPROVED,
    PICK_UP_INITIATED,
    PICK_UP_COMPLETED,
    REFUND_INITIATED,
    REFUND_COMPLETED,
    CLOSED;

    private Set<OrderStatus> allowedTransitions;

    static {
        ORDER_PLACED.allowedTransitions = EnumSet.of(
                CANCELLED, ORDER_CONFIRMED, ORDER_REJECTED
        );

        CANCELLED.allowedTransitions = EnumSet.of(
                REFUND_INITIATED, CLOSED
        );

        ORDER_REJECTED.allowedTransitions = EnumSet.of(
                REFUND_INITIATED, CLOSED
        );

        ORDER_CONFIRMED.allowedTransitions = EnumSet.of(
                CANCELLED, ORDER_SHIPPED
        );

        ORDER_SHIPPED.allowedTransitions = EnumSet.of(
                DELIVERED
        );

        DELIVERED.allowedTransitions = EnumSet.of(
                RETURN_REQUESTED, CLOSED
        );

        RETURN_REQUESTED.allowedTransitions = EnumSet.of(
                RETURN_REJECTED, RETURN_APPROVED
        );

        RETURN_REJECTED.allowedTransitions = EnumSet.of(
                CLOSED
        );

        RETURN_APPROVED.allowedTransitions = EnumSet.of(
                PICK_UP_INITIATED
        );

        PICK_UP_INITIATED.allowedTransitions = EnumSet.of(
                PICK_UP_COMPLETED
        );

        PICK_UP_COMPLETED.allowedTransitions = EnumSet.of(
                REFUND_INITIATED
        );

        REFUND_INITIATED.allowedTransitions = EnumSet.of(
                REFUND_COMPLETED
        );

        REFUND_COMPLETED.allowedTransitions = EnumSet.of(
                CLOSED
        );

        CLOSED.allowedTransitions = EnumSet.noneOf(OrderStatus.class);
    }

    public boolean canTransitionTo(OrderStatus next) {
        return allowedTransitions.contains(next);
    }

    public Set<OrderStatus> getAllowedTransitions() {
        return allowedTransitions;
    }
}
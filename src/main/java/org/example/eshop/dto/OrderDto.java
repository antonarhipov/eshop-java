package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.eshop.entity.FulfillmentStatus;
import org.example.eshop.entity.Order;
import org.example.eshop.entity.OrderStatus;
import org.example.eshop.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class OrderDto {
    private final long id;
    private final String number;
    private final String email;
    private final String fullName;
    private final String address;
    private final BigDecimal subtotal;
    private final BigDecimal tax;
    private final BigDecimal shipping;
    private final BigDecimal total;
    private final OrderStatus status;
    private final PaymentStatus paymentStatus;
    private final FulfillmentStatus fulfillmentStatus;
    private final String trackingUrl; // nullable
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<OrderItemDto> items;

    @JsonCreator
    public OrderDto(
            @JsonProperty("id") long id,
            @JsonProperty("number") String number,
            @JsonProperty("email") String email,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("address") String address,
            @JsonProperty("subtotal") BigDecimal subtotal,
            @JsonProperty("tax") BigDecimal tax,
            @JsonProperty("shipping") BigDecimal shipping,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("status") OrderStatus status,
            @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
            @JsonProperty("fulfillmentStatus") FulfillmentStatus fulfillmentStatus,
            @JsonProperty("trackingUrl") String trackingUrl,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt,
            @JsonProperty("items") List<OrderItemDto> items) {
        this.id = id;
        this.number = number;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.subtotal = subtotal;
        this.tax = tax;
        this.shipping = shipping;
        this.total = total;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.fulfillmentStatus = fulfillmentStatus;
        this.trackingUrl = trackingUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items == null ? Collections.emptyList() : List.copyOf(items);
    }

    public static OrderDto fromEntity(Order order) {
        return new OrderDto(
                order.getId(),
                order.getNumber(),
                order.getEmail(),
                order.getFullName(),
                order.getAddress(),
                order.getSubtotal(),
                order.getTax(),
                order.getShipping(),
                order.getTotal(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getFulfillmentStatus(),
                order.getTrackingUrl(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getItems().stream().map(OrderItemDto::fromEntity).toList()
        );
    }

    public long getId() { return id; }
    public String getNumber() { return number; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getAddress() { return address; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getShipping() { return shipping; }
    public BigDecimal getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public FulfillmentStatus getFulfillmentStatus() { return fulfillmentStatus; }
    public String getTrackingUrl() { return trackingUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<OrderItemDto> getItems() { return items; }
}

package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.eshop.entity.FulfillmentStatus;
import org.example.eshop.entity.OrderStatus;
import org.example.eshop.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class AdminOrderSummaryResponse {
    private final long id;
    private final String number;
    private final String email;
    private final BigDecimal total;
    private final OrderStatus status;
    private final PaymentStatus paymentStatus;
    private final FulfillmentStatus fulfillmentStatus;
    private final LocalDateTime createdAt;
    private final int itemCount;

    @JsonCreator
    public AdminOrderSummaryResponse(
            @JsonProperty("id") long id,
            @JsonProperty("number") String number,
            @JsonProperty("email") String email,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("status") OrderStatus status,
            @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
            @JsonProperty("fulfillmentStatus") FulfillmentStatus fulfillmentStatus,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("itemCount") int itemCount) {
        this.id = id;
        this.number = number;
        this.email = email;
        this.total = total;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.fulfillmentStatus = fulfillmentStatus;
        this.createdAt = createdAt;
        this.itemCount = itemCount;
    }

    public long getId() { return id; }
    public String getNumber() { return number; }
    public String getEmail() { return email; }
    public BigDecimal getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public FulfillmentStatus getFulfillmentStatus() { return fulfillmentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getItemCount() { return itemCount; }
}

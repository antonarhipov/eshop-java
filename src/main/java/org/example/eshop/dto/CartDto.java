package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CartDto {
    private final long id;
    private final BigDecimal subtotal;
    private final BigDecimal vatAmount;
    private final BigDecimal shippingCost;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<CartItemDto> items;

    @JsonCreator
    public CartDto(
            @JsonProperty("id") long id,
            @JsonProperty("subtotal") BigDecimal subtotal,
            @JsonProperty("vatAmount") BigDecimal vatAmount,
            @JsonProperty("shippingCost") BigDecimal shippingCost,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt,
            @JsonProperty("items") List<CartItemDto> items) {
        this.id = id;
        this.subtotal = subtotal;
        this.vatAmount = vatAmount;
        this.shippingCost = shippingCost;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items == null ? Collections.emptyList() : List.copyOf(items);
    }

    public long getId() { return id; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getVatAmount() { return vatAmount; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<CartItemDto> getItems() { return items; }

}

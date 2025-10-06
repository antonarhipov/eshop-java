package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class OrderItemDto {
    private final long id;
    private final long variantId;
    private final String titleSnapshot;
    private final int qty;
    private final BigDecimal priceSnapshot;
    private final BigDecimal lineTotal;

    @JsonCreator
    public OrderItemDto(
            @JsonProperty("id") long id,
            @JsonProperty("variantId") long variantId,
            @JsonProperty("titleSnapshot") String titleSnapshot,
            @JsonProperty("qty") int qty,
            @JsonProperty("priceSnapshot") BigDecimal priceSnapshot,
            @JsonProperty("lineTotal") BigDecimal lineTotal) {
        this.id = id;
        this.variantId = variantId;
        this.titleSnapshot = titleSnapshot;
        this.qty = qty;
        this.priceSnapshot = priceSnapshot;
        this.lineTotal = lineTotal;
    }

    public static OrderItemDto fromEntity(org.example.eshop.entity.OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getVariantId(),
                orderItem.getTitleSnapshot(),
                orderItem.getQty(),
                orderItem.getPriceSnapshot(),
                orderItem.getLineTotal()
        );
    }

    public long getId() { return id; }
    public long getVariantId() { return variantId; }
    public String getTitleSnapshot() { return titleSnapshot; }
    public int getQty() { return qty; }
    public BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public BigDecimal getLineTotal() { return lineTotal; }
}

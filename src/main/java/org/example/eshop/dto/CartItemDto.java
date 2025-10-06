package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class CartItemDto {
    private final long id;
    private final long variantId;
    private final int qty;
    private final BigDecimal priceSnapshot;
    private final BigDecimal lineTotal;
    private final VariantSummaryDto variant; // nullable

    @JsonCreator
    public CartItemDto(
            @JsonProperty("id") long id,
            @JsonProperty("variantId") long variantId,
            @JsonProperty("qty") int qty,
            @JsonProperty("priceSnapshot") BigDecimal priceSnapshot,
            @JsonProperty("lineTotal") BigDecimal lineTotal,
            @JsonProperty("variant") VariantSummaryDto variant) {
        this.id = id;
        this.variantId = variantId;
        this.qty = qty;
        this.priceSnapshot = priceSnapshot;
        this.lineTotal = lineTotal;
        this.variant = variant; // may be null
    }

    public long getId() { return id; }
    public long getVariantId() { return variantId; }
    public int getQty() { return qty; }
    public BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public VariantSummaryDto getVariant() { return variant; }
}

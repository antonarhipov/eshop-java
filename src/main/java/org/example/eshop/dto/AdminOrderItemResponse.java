package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class AdminOrderItemResponse {
    private final long id;
    private final long variantId;
    private final String titleSnapshot;
    private final int qty;
    private final BigDecimal priceSnapshot;
    private final BigDecimal lineTotal;
    private final String sku;
    private final String productTitle;

    @JsonCreator
    public AdminOrderItemResponse(
            @JsonProperty("id") long id,
            @JsonProperty("variantId") long variantId,
            @JsonProperty("titleSnapshot") String titleSnapshot,
            @JsonProperty("qty") int qty,
            @JsonProperty("priceSnapshot") BigDecimal priceSnapshot,
            @JsonProperty("lineTotal") BigDecimal lineTotal,
            @JsonProperty("sku") String sku,
            @JsonProperty("productTitle") String productTitle) {
        this.id = id;
        this.variantId = variantId;
        this.titleSnapshot = titleSnapshot;
        this.qty = qty;
        this.priceSnapshot = priceSnapshot;
        this.lineTotal = lineTotal;
        this.sku = sku;
        this.productTitle = productTitle;
    }

    public long getId() { return id; }
    public long getVariantId() { return variantId; }
    public String getTitleSnapshot() { return titleSnapshot; }
    public int getQty() { return qty; }
    public BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public String getSku() { return sku; }
    public String getProductTitle() { return productTitle; }
}

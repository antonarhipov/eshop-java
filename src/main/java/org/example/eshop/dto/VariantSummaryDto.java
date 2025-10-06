package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class VariantSummaryDto {
    private final long id;
    private final String sku;
    private final String title;
    private final BigDecimal price;
    private final int stockQty;
    private final int reservedQty;
    private final String productTitle;

    @JsonCreator
    public VariantSummaryDto(
            @JsonProperty("id") long id,
            @JsonProperty("sku") String sku,
            @JsonProperty("title") String title,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("stockQty") int stockQty,
            @JsonProperty("reservedQty") int reservedQty,
            @JsonProperty("productTitle") String productTitle) {
        this.id = id;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.stockQty = stockQty;
        this.reservedQty = reservedQty;
        this.productTitle = productTitle;
    }

    public long getId() { return id; }
    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public int getStockQty() { return stockQty; }
    public int getReservedQty() { return reservedQty; }
    public String getProductTitle() { return productTitle; }
}

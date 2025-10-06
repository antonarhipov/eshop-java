package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class VariantDto {
    private final long id;
    private final String sku;
    private final String title;
    private final BigDecimal price;
    private final BigDecimal weight;
    private final StockStatus stockStatus;
    private final int availableQty;
    @JsonProperty("isInStock")
    private final boolean isInStock;

    @JsonCreator
    public VariantDto(
            @JsonProperty("id") long id,
            @JsonProperty("sku") String sku,
            @JsonProperty("title") String title,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("weight") BigDecimal weight,
            @JsonProperty("stockStatus") StockStatus stockStatus,
            @JsonProperty("availableQty") int availableQty,
            @JsonProperty("isInStock") boolean isInStock) {
        this.id = id;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.weight = weight;
        this.stockStatus = stockStatus;
        this.availableQty = availableQty;
        this.isInStock = isInStock;
    }

    public long getId() { return id; }
    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getWeight() { return weight; }
    public StockStatus getStockStatus() { return stockStatus; }
    public int getAvailableQty() { return availableQty; }
    @JsonProperty("isInStock")
    public boolean isInStock() { return isInStock; }
}

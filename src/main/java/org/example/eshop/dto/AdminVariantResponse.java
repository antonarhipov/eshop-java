package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public final class AdminVariantResponse {
    private final long id;
    private final long productId;
    private final String sku;
    private final String title;
    private final BigDecimal price;
    private final BigDecimal weight;
    private final BigDecimal shippingWeight;
    private final int stockQty;
    private final int reservedQty;
    private final int availableQty;
    private final Long lotId; // nullable

    @JsonCreator
    public AdminVariantResponse(
            @JsonProperty("id") long id,
            @JsonProperty("productId") long productId,
            @JsonProperty("sku") String sku,
            @JsonProperty("title") String title,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("weight") BigDecimal weight,
            @JsonProperty("shippingWeight") BigDecimal shippingWeight,
            @JsonProperty("stockQty") int stockQty,
            @JsonProperty("reservedQty") int reservedQty,
            @JsonProperty("availableQty") int availableQty,
            @JsonProperty("lotId") Long lotId) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.weight = weight;
        this.shippingWeight = shippingWeight;
        this.stockQty = stockQty;
        this.reservedQty = reservedQty;
        this.availableQty = availableQty;
        this.lotId = lotId;
    }

    public long getId() { return id; }
    public long getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getShippingWeight() { return shippingWeight; }
    public int getStockQty() { return stockQty; }
    public int getReservedQty() { return reservedQty; }
    public int getAvailableQty() { return availableQty; }
    public Long getLotId() { return lotId; }
}

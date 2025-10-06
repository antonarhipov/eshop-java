package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class UpdateVariantRequest {
    private final String sku; // nullable
    private final String title; // nullable
    private final BigDecimal price; // nullable
    private final BigDecimal weight; // nullable
    private final BigDecimal shippingWeight; // nullable
    private final Integer stockQty; // nullable
    private final Long lotId; // nullable

    @JsonCreator
    public UpdateVariantRequest(
            @JsonProperty("sku") @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens") String sku,
            @JsonProperty("title") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
            @JsonProperty("price") @DecimalMin(value = "0.01", message = "Price must be at least 0.01") @DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99") BigDecimal price,
            @JsonProperty("weight") @DecimalMin(value = "0.001", message = "Weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Weight must not exceed 99999.999") BigDecimal weight,
            @JsonProperty("shippingWeight") @DecimalMin(value = "0.001", message = "Shipping weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Shipping weight must not exceed 99999.999") BigDecimal shippingWeight,
            @JsonProperty("stockQty") @Min(value = 0, message = "Stock quantity must not be negative") Integer stockQty,
            @JsonProperty("lotId") Long lotId
    ) {
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.weight = weight;
        this.shippingWeight = shippingWeight;
        this.stockQty = stockQty;
        this.lotId = lotId;
    }

    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getShippingWeight() { return shippingWeight; }
    public Integer getStockQty() { return stockQty; }
    public Long getLotId() { return lotId; }
}

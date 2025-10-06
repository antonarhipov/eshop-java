package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class CreateVariantRequest {
    private final long productId;
    private final String sku;
    private final String title;
    private final BigDecimal price;
    private final BigDecimal weight;
    private final BigDecimal shippingWeight;
    private final int stockQty;
    private final Long lotId; // nullable

    @JsonCreator
    public CreateVariantRequest(
            @JsonProperty("productId") @NotNull(message = "Product ID is required") @Positive(message = "Product ID must be positive") Long productId,
            @JsonProperty("sku") @NotBlank(message = "SKU is required") @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens") String sku,
            @JsonProperty("title") @NotBlank(message = "Title is required") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
            @JsonProperty("price") @NotNull(message = "Price is required") @DecimalMin(value = "0.01", message = "Price must be at least 0.01") @DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99") BigDecimal price,
            @JsonProperty("weight") @NotNull(message = "Weight is required") @DecimalMin(value = "0.001", message = "Weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Weight must not exceed 99999.999") BigDecimal weight,
            @JsonProperty("shippingWeight") @NotNull(message = "Shipping weight is required") @DecimalMin(value = "0.001", message = "Shipping weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Shipping weight must not exceed 99999.999") BigDecimal shippingWeight,
            @JsonProperty("stockQty") @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity must not be negative") Integer stockQty,
            @JsonProperty("lotId") Long lotId
    ) {
        this.productId = productId == null ? 0L : productId;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.weight = weight;
        this.shippingWeight = shippingWeight;
        this.stockQty = stockQty == null ? 0 : stockQty;
        this.lotId = lotId;
    }

    public long getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getShippingWeight() { return shippingWeight; }
    public int getStockQty() { return stockQty; }
    public Long getLotId() { return lotId; }
}

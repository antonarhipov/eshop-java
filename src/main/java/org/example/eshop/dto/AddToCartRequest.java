package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

public final class AddToCartRequest {
    private final long variantId;
    private final int quantity;

    @JsonCreator
    public AddToCartRequest(
            @JsonProperty("variantId") @Min(value = 1, message = "variantId must be a positive number") long variantId,
            @JsonProperty("quantity") @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public long getVariantId() { return variantId; }
    public int getQuantity() { return quantity; }
}

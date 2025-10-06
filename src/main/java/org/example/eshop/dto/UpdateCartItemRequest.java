package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

public final class UpdateCartItemRequest {
    private final long variantId;
    private final int quantity;

    @JsonCreator
    public UpdateCartItemRequest(
            @JsonProperty("variantId") @Min(value = 1, message = "variantId must be a positive number") long variantId,
            @JsonProperty("quantity") @Min(value = 0, message = "Quantity must be zero or greater") int quantity) {
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public long getVariantId() { return variantId; }
    public int getQuantity() { return quantity; }
}

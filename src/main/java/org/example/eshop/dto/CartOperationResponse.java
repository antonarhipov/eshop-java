package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class CartOperationResponse {
    private final boolean success;
    private final String message; // nullable
    private final CartDto cart;   // nullable
    private final List<String> errors; // default empty

    @JsonCreator
    public CartOperationResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("cart") CartDto cart,
            @JsonProperty("errors") List<String> errors) {
        this.success = success;
        this.message = message;
        this.cart = cart;
        this.errors = errors == null ? Collections.emptyList() : List.copyOf(errors);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public CartDto getCart() { return cart; }
    public List<String> getErrors() { return errors; }
}

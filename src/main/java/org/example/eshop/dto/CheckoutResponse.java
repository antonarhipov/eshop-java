package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class CheckoutResponse {
    private final boolean success;
    private final String message; // nullable
    private final OrderDto order; // nullable
    private final List<String> errors; // default empty

    @JsonCreator
    public CheckoutResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("order") OrderDto order,
            @JsonProperty("errors") List<String> errors) {
        this.success = success;
        this.message = message;
        this.order = order;
        this.errors = errors == null ? Collections.emptyList() : List.copyOf(errors);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public OrderDto getOrder() { return order; }
    public List<String> getErrors() { return errors; }
}

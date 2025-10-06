package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CancelOrderRequest {
    private final String reason; // nullable
    private final String notes;  // nullable

    @JsonCreator
    public CancelOrderRequest(
            @JsonProperty("reason") String reason,
            @JsonProperty("notes") String notes) {
        this.reason = reason;
        this.notes = notes;
    }

    public String getReason() { return reason; }
    public String getNotes() { return notes; }
}

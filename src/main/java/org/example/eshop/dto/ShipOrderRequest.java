package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public final class ShipOrderRequest {
    private final String trackingUrl; // nullable
    private final String notes; // nullable

    @JsonCreator
    public ShipOrderRequest(
            @JsonProperty("trackingUrl") @Size(max = 500, message = "Tracking URL must not exceed 500 characters") String trackingUrl,
            @JsonProperty("notes") String notes) {
        this.trackingUrl = trackingUrl;
        this.notes = notes;
    }

    public String getTrackingUrl() { return trackingUrl; }
    public String getNotes() { return notes; }
}

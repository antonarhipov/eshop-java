package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MarkOrderPaidRequest {
    private final String notes; // nullable

    public MarkOrderPaidRequest() {
        this.notes = null;
    }

    @JsonCreator
    public MarkOrderPaidRequest(@JsonProperty("notes") String notes) {
        this.notes = notes;
    }

    public String getNotes() { return notes; }
}

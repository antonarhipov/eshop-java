package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.example.eshop.entity.Season;
import org.example.eshop.entity.StorageType;

import java.time.LocalDate;

public final class CreateLotRequest {
    private final long productId;
    private final int harvestYear;
    private final Season season;
    private final StorageType storageType;
    private final LocalDate pressDate; // nullable

    @JsonCreator
    public CreateLotRequest(
            @JsonProperty("productId") @NotNull(message = "Product ID is required") @Positive(message = "Product ID must be positive") Long productId,
            @JsonProperty("harvestYear") @NotNull(message = "Harvest year is required") @Min(value = 1900, message = "Harvest year must be at least 1900") @Max(value = 2030, message = "Harvest year must not exceed 2030") Integer harvestYear,
            @JsonProperty("season") @NotNull(message = "Season is required") Season season,
            @JsonProperty("storageType") @NotNull(message = "Storage type is required") StorageType storageType,
            @JsonProperty("pressDate") LocalDate pressDate
    ) {
        this.productId = productId == null ? 0L : productId;
        this.harvestYear = harvestYear == null ? 0 : harvestYear;
        this.season = season;
        this.storageType = storageType;
        this.pressDate = pressDate;
    }

    public long getProductId() { return productId; }
    public int getHarvestYear() { return harvestYear; }
    public Season getSeason() { return season; }
    public StorageType getStorageType() { return storageType; }
    public LocalDate getPressDate() { return pressDate; }
}

package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.eshop.entity.Season;
import org.example.eshop.entity.StorageType;

import java.time.LocalDate;

public final class UpdateLotRequest {
    private final Integer harvestYear; // nullable
    private final Season season; // nullable
    private final StorageType storageType; // nullable
    private final LocalDate pressDate; // nullable

    @JsonCreator
    public UpdateLotRequest(
            @JsonProperty("harvestYear") @Min(value = 1900, message = "Harvest year must be at least 1900") @Max(value = 2030, message = "Harvest year must not exceed 2030") Integer harvestYear,
            @JsonProperty("season") Season season,
            @JsonProperty("storageType") StorageType storageType,
            @JsonProperty("pressDate") LocalDate pressDate
    ) {
        this.harvestYear = harvestYear;
        this.season = season;
        this.storageType = storageType;
        this.pressDate = pressDate;
    }

    public Integer getHarvestYear() { return harvestYear; }
    public Season getSeason() { return season; }
    public StorageType getStorageType() { return storageType; }
    public LocalDate getPressDate() { return pressDate; }
}

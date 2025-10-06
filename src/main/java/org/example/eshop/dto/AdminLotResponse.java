package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.eshop.entity.Season;
import org.example.eshop.entity.StorageType;

import java.time.LocalDate;

public final class AdminLotResponse {
    private final long id;
    private final long productId;
    private final int harvestYear;
    private final Season season;
    private final StorageType storageType;
    private final LocalDate pressDate; // nullable
    private final int variantCount;

    @JsonCreator
    public AdminLotResponse(
            @JsonProperty("id") long id,
            @JsonProperty("productId") long productId,
            @JsonProperty("harvestYear") int harvestYear,
            @JsonProperty("season") Season season,
            @JsonProperty("storageType") StorageType storageType,
            @JsonProperty("pressDate") LocalDate pressDate,
            @JsonProperty("variantCount") int variantCount) {
        this.id = id;
        this.productId = productId;
        this.harvestYear = harvestYear;
        this.season = season;
        this.storageType = storageType;
        this.pressDate = pressDate;
        this.variantCount = variantCount;
    }

    public long getId() { return id; }
    public long getProductId() { return productId; }
    public int getHarvestYear() { return harvestYear; }
    public Season getSeason() { return season; }
    public StorageType getStorageType() { return storageType; }
    public LocalDate getPressDate() { return pressDate; }
    public int getVariantCount() { return variantCount; }
}

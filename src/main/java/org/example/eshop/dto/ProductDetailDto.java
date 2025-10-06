package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class ProductDetailDto {
    private final long id;
    private final String slug;
    private final String title;
    private final String type;
    private final String description; // nullable
    private final List<VariantDto> variants;
    private final Integer harvestYear; // nullable
    private final String season; // nullable
    private final String storageType; // nullable

    @JsonCreator
    public ProductDetailDto(
            @JsonProperty("id") long id,
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("variants") List<VariantDto> variants,
            @JsonProperty("harvestYear") Integer harvestYear,
            @JsonProperty("season") String season,
            @JsonProperty("storageType") String storageType) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.type = type;
        this.description = description;
        this.variants = variants;
        this.harvestYear = harvestYear;
        this.season = season;
        this.storageType = storageType;
    }

    public long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public List<VariantDto> getVariants() { return variants; }
    public Integer getHarvestYear() { return harvestYear; }
    public String getSeason() { return season; }
    public String getStorageType() { return storageType; }
}

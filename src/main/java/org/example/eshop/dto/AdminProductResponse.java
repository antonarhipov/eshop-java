package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.eshop.entity.ProductStatus;

public final class AdminProductResponse {
    private final long id;
    private final String slug;
    private final String title;
    private final String type;
    private final String description; // nullable
    private final ProductStatus status;
    private final int variantCount;
    private final int lotCount;

    @JsonCreator
    public AdminProductResponse(
            @JsonProperty("id") long id,
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("status") ProductStatus status,
            @JsonProperty("variantCount") int variantCount,
            @JsonProperty("lotCount") int lotCount) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.type = type;
        this.description = description;
        this.status = status;
        this.variantCount = variantCount;
        this.lotCount = lotCount;
    }

    public long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public ProductStatus getStatus() { return status; }
    public int getVariantCount() { return variantCount; }
    public int getLotCount() { return lotCount; }
}
